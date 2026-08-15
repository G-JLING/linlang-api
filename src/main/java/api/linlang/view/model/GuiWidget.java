package api.linlang.view.model;

import api.linlang.view.model.dto.GuiIcon;

import java.util.Map;

/**
 * 可渲染、可交互的界面控件。
 */
public interface GuiWidget {

    /**
     * @return 稳定 ID；未声明时为空字符串
     */
    default String id() { return ""; }

    /**
     * @return 控件图标
     */
    GuiIcon icon();

    /**
     * @return 点击动作；不可交互时可为 {@code null}
     */
    GuiAction action();

    /**
     * @return 是否渲染控件
     */
    default boolean visible() { return true; }

    /**
     * @return 是否允许执行点击动作
     */
    default boolean enabled() { return true; }

    /**
     * @return 供平台实现使用的附加数据
     */
    default Map<String, Object> meta() { return Map.of(); }

    /**
     * 创建始终可见且可点击的简单控件。
     *
     * @param icon 控件图标
     * @param action 点击动作，可为 {@code null}
     * @return 新控件
     */
    static GuiWidget of(GuiIcon icon, GuiAction action) {
        return new Simple(icon, action);
    }

    /**
     * {@link GuiWidget} 的简单不可变实现。
     */
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
