package api.linlang.banner.provider;

import api.linlang.banner.service.AsciiFont;

/**
 * 通过 {@link java.util.ServiceLoader} 向 Banner 服务提供 ASCII 字体。
 */
public interface BannerFontProvider {
    /**
     * @return 可用字体；无法提供时可返回 {@code null}
     */
    AsciiFont font();
}
