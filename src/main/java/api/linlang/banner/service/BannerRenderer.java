package api.linlang.banner.service;

import api.linlang.banner.BannerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Banner 的底层文本渲染器。
 *
 * @hidden
 */
public final class BannerRenderer {
    private BannerRenderer() {}

    /**
     * 渲染 ASCII 缩写和固定信息块，并逐行输出。
     *
     * @param font ASCII 字体
     * @param opt 铭牌选项
     * @param out 行输出函数
     */
    public static void print(AsciiFont font, BannerOptions opt, Consumer<String> out) {
        if (font == null || opt == null || out == null) return;

        // 顶部空行
        out.accept(" ");

        // 左侧 ASCII 牌头
        List<String> art = renderWord(font, opt.initials());

        // 右侧信息行（与 ASCII 行并排打印），中间空 4 个空格
        String devLine = String.join(", ", opt.developers());

        List<String> info = new ArrayList<>();
        info.add((opt.pluginZh() == null ? " " : opt.pluginZh()) + " " + opt.pluginEn() + " " + opt.version());
        info.add("By: " + devLine);
        info.add(opt.site() == null ? " " : opt.site());
        info.add(" ");
        info.add("MagicPowered | Linlang | https://jling.me");

        int rows = Math.max(art.size(), info.size());
        for (int i = 0; i < rows; i++) {
            String left  = i < art.size()  ? art.get(i)  : pad(font);
            String right = i < info.size() ? info.get(i) : "";
            out.accept(left + "    " + right);
        }

        // 底部空行
        out.accept(" ");
    }

    /**
     * 将单词渲染为 ASCII 行集合。
     *
     * @param font ASCII 字体
     * @param word 待渲染单词
     * @return 按从上到下顺序排列的文本行
     */
    public static List<String> renderWord(AsciiFont font, String word) {
        int h = font.height();
        List<String> out = new ArrayList<>(h);
        for (int r = 0; r < h; r++) {
            StringBuilder sb = new StringBuilder("  ");
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                List<String> g = font.glyph(ch);
                String piece = r < g.size() ? g.get(r) : "";
                sb.append(piece);
                if (i < word.length() - 1) sb.append(" ".repeat(font.gap()));
            }
            out.add(sb.toString());
        }
        return out;
    }

    private static String pad(AsciiFont font) {
        // 与牌头左缩进对齐
        return "  ";
    }
}
