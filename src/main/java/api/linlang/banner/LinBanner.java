package api.linlang.banner;

import api.linlang.audit.LinLog;
import api.linlang.banner.provider.BannerFontProvider;
import api.linlang.banner.service.AsciiFont;
import api.linlang.banner.service.BannerRenderer;

import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * ASCII 启动铭牌打印入口。
 *
 * <p>字体通过 {@link java.util.ServiceLoader} 发现的 {@link BannerFontProvider} 提供，
 * 首个成功返回字体的提供者会被缓存。</p>
 */
public final class LinBanner {
    private static volatile AsciiFont CACHED;

    private LinBanner() {}

    /**
     * 将铭牌逐行写入带日志前缀的 INFO 通道。
     *
     * @param opt 铭牌选项
     */
    public static void printWithPrefix(BannerOptions opt) {
        print(LinLog::info, opt);
    }
    /**
     * 将铭牌逐行写入专用 Banner 日志通道。
     *
     * @param opt 铭牌选项
     */
    public static void print(BannerOptions opt) {
        print(LinLog::banr, opt);
    }

    /**
     * 将铭牌逐行写入自定义输出。
     *
     * <p>未发现可用字体时不调用输出，并记录警告。</p>
     *
     * @param sink 行输出函数
     * @param opt 铭牌选项
     */
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

    /**
     * @return 新的铭牌选项构建器
     */
    public static BannerOptions.Builder options() {
        return BannerOptions.builder();
    }
}
