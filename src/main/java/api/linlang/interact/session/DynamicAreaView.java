package api.linlang.interact.session;

import api.linlang.interact.model.GuiRow;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 动态区：一组按顺序排列的可填充槽位（slot stack）。
 */
public interface DynamicAreaView {

    /** 动态区 id。 */
    String id();

    /** 容量（slot 数量）。 */
    int capacity();

    /** 清空该区域（解绑 rowRef）。 */
    DynamicAreaView clear();

    /** 顺序填充：从第 0 个槽位开始。 */
    DynamicAreaView fill(List<? extends GuiRow> rows);

    /** 设置某一个 index 的内容（0 <= index < capacity）。 */
    DynamicAreaView set(int index, GuiRow row);

    /** 追加一个元素（若超出容量，按实现的 overflow 策略处理）。 */
    DynamicAreaView push(GuiRow row);

    /** 获取某个 index 的绑定（可能为 null）。 */
    GuiRow get(int index);

    /** 遍历当前绑定。 */
    void forEach(BiConsumer<Integer, GuiRow> consumer);
}