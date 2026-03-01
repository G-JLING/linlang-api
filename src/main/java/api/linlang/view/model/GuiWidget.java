package api.linlang.view.model;

import api.linlang.view.model.dto.GuiIcon;

import java.util.Map;

/**
 * 一个可选染的界面组建类型
 */
public interface GuiWidget {

    /** 稳定 id（静态区用 UID；动态区可为空或用于调试）。 */
    default String id() { return ""; }

    /** 图标/外观描述。 */
    GuiIcon icon();

    /** 点击动作（可为空）。 */
    GuiAction action();

    /** 是否可见。 */
    default boolean visible() { return true; }

    /** 是否可点击。 */
    default boolean enabled() { return true; }

    /** 附加数据（高级用法）。 */
    default Map<String, Object> meta() { return Map.of(); }

    /** 一个简单实现。 */
    static GuiWidget of(GuiIcon icon, GuiAction action) {
        return new Simple(icon, action);
    }

    final class Simple implements GuiWidget {
        private final GuiIcon icon;
        private final GuiAction action;

        Simple(GuiIcon icon, GuiAction action) {
            this.icon = icon;
            this.action = action;
        }

        @Override public GuiIcon icon() { return icon; }
        @Override public GuiAction action() { return action; }
    }
}