package api.linlang.view.session;

import api.linlang.view.model.GuiWidget;

import java.util.Map;
import java.util.Set;

/**
 * 由视图资源定义并通过稳定 UID 访问的静态控件集合。
 */
public interface StaticView {

    /**
     * @return 当前视图中全部静态控件 UID
     */
    Set<String> ids();

    /**
     * 获取当前有效控件。
     *
     * <p>存在代码覆盖时返回覆盖值，否则返回资源文件中的默认控件。</p>
     *
     * @param uid 静态控件 UID
     * @return 当前有效控件；不存在时为 {@code null}
     */
    GuiWidget get(String uid);

    /**
     * 覆盖静态控件的图标与动作。
     *
     * <p>{@code widget} 为 {@code null} 时移除代码覆盖并恢复资源默认值。</p>
     *
     * @param uid 静态控件 UID
     * @param widget 新控件或 {@code null}
     * @return 当前静态区
     */
    StaticView set(String uid, GuiWidget widget);

    /**
     * 设置静态控件是否可见。
     *
     * @param uid 静态控件 UID
     * @param visible 是否可见
     * @return 当前静态区
     */
    StaticView visible(String uid, boolean visible);

    /**
     * 设置静态控件是否可点击。
     *
     * @param uid 静态控件 UID
     * @param enabled 是否可点击
     * @return 当前静态区
     */
    StaticView enabled(String uid, boolean enabled);

    /**
     * 批量覆盖静态控件。
     *
     * @param widgets UID 到控件的映射
     * @return 当前静态区
     */
    StaticView setAll(Map<String, GuiWidget> widgets);
}
