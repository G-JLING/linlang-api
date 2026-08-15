package api.linlang.banner.service;

import java.util.List;
import java.util.Map;

/**
 * ASCII 字体描述，包含高度、字距与每个字符的 ASCII 图形。
 */
public interface AsciiFont {

    /**
     * @return 字体高度，即每个字符的行数
     */
    int height();

    /**
     * @return 字符之间的空格数量
     */
    int gap();

    /**
     * 返回指定字符的图形。
     *
     * @param ch 字符
     * @return 按从上到下顺序排列的图形行；未定义时为空列表
     */
    List<String> glyph(char ch);

    /**
     * @return 新的字体构造器
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * ASCII 字体构建器。
     */
    final class Builder {
        private int height = 5;
        private int gap = 1;
        private final Map<Character, List<String>> glyphs = new java.util.LinkedHashMap<>();

        /**
         * 设置字体高度。
         *
         * @param h 字体行数
         * @return 当前构建器
         */
        public Builder height(int h) {
            this.height = h;
            return this;
        }

        /**
         * 设置字符间距。
         *
         * @param g 空格数量
         * @return 当前构建器
         */
        public Builder gap(int g) {
            this.gap = g;
            return this;
        }

        /**
         * 添加字符图形。
         *
         * @param ch 字符
         * @param lines 按从上到下顺序排列的图形行
         * @return 当前构建器
         */
        public Builder put(char ch, List<String> lines) {
            glyphs.put(ch, List.copyOf(lines));
            return this;
        }

        /**
         * @return 不可变的 ASCII 字体
         */
        public AsciiFont build() {
            int h = this.height;
            int g = this.gap;
            Map<Character, List<String>> map = Map.copyOf(glyphs);
            return new AsciiFont() {
                @Override
                public int height() {
                    return h;
                }

                @Override
                public int gap() {
                    return g;
                }

                @Override
                public List<String> glyph(char ch) {
                    return map.getOrDefault(ch, List.of());
                }
            };
        }
    }
}
