package api.linlang.runtime.version;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检测插件编译时使用的 Linlang API 版本。
 *
 * <p>Maven 构建通常会把项目 POM 写入插件 Jar，本类会读取其中的
 * {@code me.jling:linlang-api} 依赖。其他构建系统可以在插件 Jar 中提供
 * {@value #VERSION_RESOURCE}，并使用 {@code version} 键声明版本。</p>
 */
public final class ApiVersionDetector {

    public static final String VERSION_RESOURCE = "META-INF/linlang/required-api.properties";
    private static final Pattern PROPERTY = Pattern.compile("\\$\\{([^}]+)}");

    private ApiVersionDetector() {
    }

    /**
     * 从宿主对象所属的构建产物中检测 API 版本。
     *
     * @param owner 插件主类实例或其他宿主对象
     * @return 检测到的有效版本
     */
    public static Optional<String> detect(Object owner) {
        if (owner == null) return Optional.empty();
        try {
            var protectionDomain = owner.getClass().getProtectionDomain();
            if (protectionDomain == null || protectionDomain.getCodeSource() == null
                    || protectionDomain.getCodeSource().getLocation() == null) {
                return Optional.empty();
            }
            return detect(Path.of(protectionDomain.getCodeSource().getLocation().toURI()));
        } catch (URISyntaxException | IllegalArgumentException | SecurityException exception) {
            return Optional.empty();
        }
    }

    static Optional<String> detect(Path location) {
        if (location == null) return Optional.empty();
        if (Files.isRegularFile(location)) return detectArchive(location);
        if (!Files.isDirectory(location)) return Optional.empty();

        Path current = location;
        for (int depth = 0; depth < 6 && current != null; depth++, current = current.getParent()) {
            Path pom = current.resolve("pom.xml");
            if (!Files.isRegularFile(pom)) continue;
            try (InputStream stream = Files.newInputStream(pom)) {
                Optional<String> version = detectPom(stream);
                if (version.isPresent()) return version;
            } catch (Exception ignored) {
            }
        }
        return Optional.empty();
    }

    private static Optional<String> detectArchive(Path archive) {
        try (JarFile jar = new JarFile(archive.toFile())) {
            JarEntry marker = jar.getJarEntry(VERSION_RESOURCE);
            if (marker != null) {
                try (InputStream stream = jar.getInputStream(marker)) {
                    Optional<String> version = detectProperties(stream);
                    if (version.isPresent()) return version;
                }
            }

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith("META-INF/maven/")
                        || !entry.getName().endsWith("/pom.xml")) continue;
                try (InputStream stream = jar.getInputStream(entry)) {
                    Optional<String> version = detectPom(stream);
                    if (version.isPresent()) return version;
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    private static Optional<String> detectProperties(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return valid(properties.getProperty("version"));
    }

    private static Optional<String> detectPom(InputStream stream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Element project = factory.newDocumentBuilder().parse(stream).getDocumentElement();
        Map<String, String> properties = readProperties(project);
        putProjectProperty(properties, "project.version", project, "version");
        putProjectProperty(properties, "pom.version", project, "version");
        putProjectProperty(properties, "project.groupId", project, "groupId");
        putProjectProperty(properties, "project.artifactId", project, "artifactId");

        NodeList dependencies = project.getElementsByTagName("dependency");
        for (int index = 0; index < dependencies.getLength(); index++) {
            if (!(dependencies.item(index) instanceof Element dependency)) continue;
            String group = resolve(childText(dependency, "groupId"), properties);
            String artifact = resolve(childText(dependency, "artifactId"), properties);
            if (!"me.jling".equals(group) || !"linlang-api".equals(artifact)) continue;
            Optional<String> version = valid(resolve(childText(dependency, "version"), properties));
            if (version.isPresent()) return version;
        }
        return Optional.empty();
    }

    private static Map<String, String> readProperties(Element project) {
        Map<String, String> properties = new LinkedHashMap<>();
        Element container = child(project, "properties");
        if (container == null) return properties;
        NodeList children = container.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            if (child instanceof Element element) {
                properties.put(element.getTagName(), element.getTextContent().trim());
            }
        }
        return properties;
    }

    private static void putProjectProperty(Map<String, String> properties, String key,
                                           Element project, String childName) {
        String value = childText(project, childName);
        if (!value.isBlank()) properties.putIfAbsent(key, value);
    }

    private static String resolve(String value, Map<String, String> properties) {
        String resolved = value == null ? "" : value.trim();
        for (int depth = 0; depth < 8; depth++) {
            Matcher matcher = PROPERTY.matcher(resolved);
            StringBuffer output = new StringBuffer();
            boolean changed = false;
            while (matcher.find()) {
                String replacement = properties.get(matcher.group(1));
                if (replacement == null) continue;
                matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
                changed = true;
            }
            if (!changed) return resolved;
            matcher.appendTail(output);
            resolved = output.toString().trim();
        }
        return resolved;
    }

    private static String childText(Element parent, String name) {
        Element child = child(parent, name);
        return child == null ? "" : child.getTextContent().trim();
    }

    private static Element child(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            if (child instanceof Element element && name.equals(element.getTagName())) {
                return element;
            }
        }
        return null;
    }

    private static Optional<String> valid(String version) {
        if (version == null || version.isBlank()) return Optional.empty();
        String value = version.trim();
        try {
            LinVersion.parse(value);
            return Optional.of(value);
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
