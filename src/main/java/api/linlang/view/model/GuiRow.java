package api.linlang.view.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 动态区中的一行渲染数据。
 *
 * <p>{@link #widget()} 为 {@code null} 时使用资源文件中的模板和变体；不为空时由该控件
 * 覆盖模板。{@link #data()} 同时用于 {@code row.*} 占位符和点击 Hook 的原始数据。</p>
 */
public interface GuiRow {

    /**
     * @return 行级控件覆盖；使用资源模板时为 {@code null}
     */
    GuiWidget widget();

    /**
     * @return 行绑定数据的只读映射
     */
    Map<String, Object> data();

    /**
     * 创建动态行。
     *
     * @param widget 行级控件覆盖，可为 {@code null}
     * @param data 行绑定数据，可为 {@code null}
     * @return 新的动态行
     */
    static GuiRow of(GuiWidget widget, Map<String, Object> data) {
        return new Simple(widget, data);
    }

    /**
     * {@link GuiRow} 的简单不可变实现。
     */
    final class Simple implements GuiRow {
        private final GuiWidget widget;
        private final Map<String, Object> data;

        Simple(GuiWidget widget, Map<String, Object> data) {
            this.widget = widget;
            this.data = data == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(data));
        }

        @Override public GuiWidget widget() { return widget; }
        @Override public Map<String, Object> data() { return data; }
    }
}
