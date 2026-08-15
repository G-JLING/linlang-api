package api.linlang.view.model.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 与平台无关的界面图标描述。
 *
 * <p>字符串字段与 {@code meta} 中的字符串允许在渲染时使用 LinView 占位符。</p>
 *
 * @param kind 图标解析器类型，例如 {@code vanilla} 或 {@code itemsadder}
 * @param key 图标资源键，例如 Bukkit Material 名称或 ItemsAdder ID
 * @param amount 物品数量；为 {@code null} 时由平台使用默认值
 * @param name 显示名称
 * @param lore Lore 文本列表
 * @param meta 平台解析器附加数据
 */
public record GuiIcon(
        String kind,                 // "vanilla" | "itemsadder" | "head" | "custom"
        String key,                  // "minecraft:DIAMOND" or "itemsadder:ns:id"
        Integer amount,
        String name,                 // 支持 placeholders
        List<String> lore,           // 支持 placeholders
        Map<String, Object> meta     // customModelData, nbt patch, etc.
) {
    /**
     * 规范化集合字段并创建图标描述。
     *
     * @param kind 图标解析器类型
     * @param key 图标资源键
     * @param amount 物品数量
     * @param name 显示名称
     * @param lore Lore 文本列表
     * @param meta 平台解析器附加数据
     */
    public GuiIcon {
        lore = lore == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(lore));
        meta = meta == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(meta));
    }

    /**
     * 创建原版材质图标。
     *
     * @param material 原版材质名称
     * @param name 显示名称
     * @param lore Lore 文本
     * @return 原版图标
     */
    public static GuiIcon vanilla(String material, String name, List<String> lore) {
        return new GuiIcon("vanilla", material, 1, name, lore, Map.of());
    }

    /**
     * 创建 ItemsAdder 图标。
     *
     * @param iaId ItemsAdder 资源 ID
     * @param name 显示名称
     * @param lore Lore 文本
     * @return ItemsAdder 图标
     */
    public static GuiIcon itemsAdder(String iaId, String name, List<String> lore) {
        return new GuiIcon("itemsadder", iaId, 1, name, lore, Map.of());
    }
}
