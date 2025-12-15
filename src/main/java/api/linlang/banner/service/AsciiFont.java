package api.linlang.banner.service;

import java.util.List;
import java.util.Map;

/**
 * ASCII 字体描述，包含高度、字距与每个字符的 ASCII 图形。
 */
public interface AsciiFont {

    /** 字体高度（行数） */
    int height();

    /** 字符间的水平间距（空格数） */
    int gap();

    /** 返回给定字符的 ASCII 图形，每个元素是一行 */
    List<String> glyph(char ch);

    /** 创建一个新的字体构造器 */
    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private int height = 5;
        private int gap = 1;
        private final Map<Character, List<String>> glyphs = new java.util.LinkedHashMap<>();

        public Builder height(int h) {
            this.height = h;
            return this;
        }

        public Builder gap(int g) {
            this.gap = g;
            return this;
        }

        public Builder put(char ch, List<String> lines) {
            glyphs.put(ch, List.copyOf(lines));
            return this;
        }

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