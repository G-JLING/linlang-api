package api.linlang.banner;

import api.linlang.audit.called.LinLog;

import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class LinBanner {
    private static volatile AsciiFont CACHED;

    private LinBanner() {}

    /** 打印到 LinLog（INFO） */
    public static void printWithLogs(BannerOptions opt) {
        BannerRenderer.print(font(), opt, LinLog::info);
    }

    /** 打印到自定义输出（如 Bukkit Logger），更通用 */
    public static void print(Consumer<String> sink, BannerOptions opt) {
        BannerRenderer.print(font(), opt, sink);
    }

    /** 通过 SPI 拿字体，失败则回退到内建默认字体 */
    private static AsciiFont font() {
        AsciiFont f = CACHED;
        if (f != null) return f;
        for (BannerFontProvider p : ServiceLoader.load(BannerFontProvider.class)) {
            f = p.font();
            if (f != null) { CACHED = f; return f; }
        }
        return f;
    }

    public static BannerOptions.Builder options() { return BannerOptions.builder(); }
}