package api.linlang.banner;

import java.util.List;

/**
 * ASCII 启动铭牌渲染选项。
 *
 * @param initials 用于渲染 ASCII 图形的缩写
 * @param pluginZh 插件中文名，可为 {@code null}
 * @param pluginEn 插件英文名
 * @param version 插件版本
 * @param developers 开发者列表
 * @param site 项目网站
 * @hidden
 */
public record BannerOptions(
        String initials,      // 牌头 ASCII 字母，如 "MP"
        String pluginZh, String pluginEn, String version,
        List<String> developers, String site
) {
    public BannerOptions {
        developers = developers == null ? List.of() : List.copyOf(developers);
    }

    /**
     * @return 新的铭牌选项构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 铭牌选项构建器。
     */
    public static final class Builder {
        private String initials = "MP";
        private String pluginZh = null, pluginEn = "PluginName", version = "v1.0.0.0-SNAPSHOT";
        private String site = "https://jling.me";
        private java.util.List<String> developers = java.util.List.of("JLING");

        /**
         * 设置 ASCII 缩写。
         *
         * @param v 缩写文本
         * @return 当前构建器
         */
        public Builder initials(String v) {
            initials = v;
            return this;
        }

        /**
         * 设置插件名称与版本。
         *
         * @param zh 中文名，可为 {@code null}
         * @param en 英文名
         * @param ver 版本文本
         * @return 当前构建器
         */
        public Builder plugin(String zh, String en, String ver) {
            pluginZh = zh;
            pluginEn = en;
            version = ver;
            return this;
        }

        /**
         * 设置项目网站。
         *
         * @param v 网站文本
         * @return 当前构建器
         */
        public Builder site(String v) {
            site = v;
            return this;
        }

        /**
         * 设置开发者列表。
         *
         * @param devs 开发者列表；为 {@code null} 时按空列表处理
         * @return 当前构建器
         */
        public Builder developers(List<String> devs) {
            developers = devs == null ? List.of() : List.copyOf(devs);
            return this;
        }

        /**
         * @return 不可变的铭牌选项
         */
        public BannerOptions build() {
            return new BannerOptions(initials, pluginZh, pluginEn, version, developers, site);
        }
    }
}
