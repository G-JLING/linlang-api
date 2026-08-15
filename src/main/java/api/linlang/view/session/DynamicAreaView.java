package api.linlang.view.session;

import api.linlang.view.model.GuiRow;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 一组按布局顺序排列、可绑定 {@link GuiRow} 的动态槽位。
 */
public interface DynamicAreaView {

    /**
     * @return 动态区 ID
     */
    String id();

    /**
     * @return 动态区槽位数量
     */
    int capacity();

    /**
     * 清空动态区并解除全部行绑定。
     *
     * @return 当前动态区
     */
    DynamicAreaView clear();

    /**
     * 按区域的 overflow 策略顺序填充行。
     *
     * @param rows 待填充行；为 {@code null} 时仅清空区域
     * @return 当前动态区
     */
    DynamicAreaView fill(List<? extends GuiRow> rows);

    /**
     * 设置指定区域索引的行。
     *
     * @param index 从零开始的区域索引
     * @param row 新行；为 {@code null} 时清除该位置
     * @return 当前动态区；索引越界时不执行操作
     */
    DynamicAreaView set(int index, GuiRow row);

    /**
     * 在当前绑定末尾追加一行。
     *
     * @param row 待追加行
     * @return 当前动态区；容量已满时不执行操作
     */
    DynamicAreaView push(GuiRow row);

    /**
     * 获取指定区域索引的行绑定。
     *
     * @param index 从零开始的区域索引
     * @return 行绑定；未绑定或索引越界时为 {@code null}
     */
    GuiRow get(int index);

    /**
     * 按区域索引遍历当前行绑定。
     *
     * @param consumer 接收区域索引和行的回调
     */
    void forEach(BiConsumer<Integer, GuiRow> consumer);
}
