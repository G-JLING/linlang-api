package api.linlang.banner;

import java.util.List;

/** @hidden */
public record BannerOptions(
        String initials,      // 牌头 ASCII 字母，如 "MP"
        String pluginZh, String pluginEn, String version,
        List<String> developers, String site
) {
    public BannerOptions {
        developers = developers == null ? List.of() : List.copyOf(developers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String initials = "MP";
        private String pluginZh = null, pluginEn = "PluginName", version = "v1.0.0.0-SNAPSHOT";
        private String site = "https://jling.me";
        private java.util.List<String> developers = java.util.List.of("JLING");

        public Builder initials(String v) {
            initials = v;
            return this;
        }

        public Builder plugin(String zh, String en, String ver) {
            pluginZh = zh;
            pluginEn = en;
            version = ver;
            return this;
        }

        public Builder site(String v) {
            site = v;
            return this;
        }

        public Builder developers(List<String> devs) {
            developers = devs == null ? List.of() : List.copyOf(devs);
            return this;
        }

        public BannerOptions build() {
            return new BannerOptions(initials, pluginZh, pluginEn, version, developers, site);
        }
    }
}
