package api.linlang.interact.model;

import java.util.Map;

/**
 * 动态区的一行/一个元素。
 *
 * <p>包含：渲染用 widget + 行绑定数据（RowRef）。点击时能取到 row.data。</p>
 */
public interface GuiRow {

    /** 渲染用控件。 */
    GuiWidget widget();

    /** 行绑定数据（用于 click hook）。 */
    Map<String, Object> data();

    static GuiRow of(GuiWidget widget, Map<String, Object> data) {
        return new Simple(widget, data);
    }

    final class Simple implements GuiRow {
        private final GuiWidget widget;
        private final Map<String, Object> data;

        Simple(GuiWidget widget, Map<String, Object> data) {
            this.widget = widget;
            this.data = data == null ? Map.of() : data;
        }

        @Override public GuiWidget widget() { return widget; }
        @Override public Map<String, Object> data() { return data; }
    }
}