package api.linlang.banner;

import api.linlang.audit.LinLog;
import api.linlang.banner.provider.BannerFontProvider;
import api.linlang.banner.service.AsciiFont;
import api.linlang.banner.service.BannerRenderer;

import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * ASCII 铭牌打印入口
 *
 * <p>通过 SPI 发现 {@link BannerFontProvider} 并渲染 ASCII 铭牌</p>
 */
public final class LinBanner {
    private static volatile AsciiFont CACHED;

    private LinBanner() {}

    /** 打印到 LinLog（INFO） */
    public static void printWithPrefix(BannerOptions opt) {
        print(LinLog::info, opt);
    }
    public static void print(BannerOptions opt) {
        print(LinLog::banr, opt);
    }

    /** 打印到自定义输出 */
    public static void print(Consumer<String> sink, BannerOptions opt) {
        AsciiFont font = resolveFont();
        if (font == null) {
            LinLog.warn("[linbanner] no ascii font; skipping banner print.");
            return;
        }
        BannerRenderer.print(font, opt, sink);
    }

    /** 通过 SPI 拿字体；若无可用字体则返回 null */
    private static AsciiFont resolveFont() {
        AsciiFont cached = CACHED;
        if (cached != null) return cached;

        ClassLoader cl = LinBanner.class.getClassLoader();
        ServiceLoader<BannerFontProvider> loader =
                ServiceLoader.load(BannerFontProvider.class, cl);

        int providers = 0;
        for (BannerFontProvider p : loader) {
            providers++;
            try {
                AsciiFont f = p.font();
                if (f != null) {
                    CACHED = f;
                    return f;
                }
            } catch (Throwable t) {
                LinLog.warn(
                        "[linbanner] BannerFontProvider {} failed to supply font: {}",
                        p.getClass().getName(), t.toString()
                );
            }
        }

        if (providers == 0) {
            LinLog.warn("[linbanner] no BannerFontProvider discovered via ServiceLoader; skipping banner print.");
        }
        return null;
    }

    public static BannerOptions.Builder options() {
        return BannerOptions.builder();
    }
}