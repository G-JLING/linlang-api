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
    public static void printWithLogs(BannerOptions opt) {
        print(LinLog::info, opt);
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
        AsciiFont f = CACHED;
        if (f != null) return f;

        for (BannerFontProvider p : ServiceLoader.load(BannerFontProvider.class)) {
            try {
                f = p.font();
                if (f != null) {
                    CACHED = f;
                    return f;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public static BannerOptions.Builder options() {
        return BannerOptions.builder();
    }
}