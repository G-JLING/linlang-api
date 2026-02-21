package api.linlang.interact.session;

import api.linlang.interact.model.GuiWidget;

import java.util.Map;
import java.util.Set;

/**
 * 静态区：由配置文件定义并拥有稳定 UID 的控件集合。
 */
public interface StaticView {

    /** 所有静态控件 UID。 */
    Set<String> ids();

    /** 取一个静态控件（不存在返回 null）。 */
    GuiWidget get(String uid);

    /** 覆盖/更新静态控件内容（例如修改 icon/lore/可见性）。 */
    StaticView set(String uid, GuiWidget widget);

    /** 更新静态控件的可见性。 */
    StaticView visible(String uid, boolean visible);

    /** 更新静态控件的可点击性。 */
    StaticView enabled(String uid, boolean enabled);

    /** 批量更新。 */
    StaticView setAll(Map<String, GuiWidget> widgets);
}