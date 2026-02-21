package api.linlang.interact.model.dto;

import java.util.List;
import java.util.Map;

/**
 * 图标描述（平台无关），由 adapter/resolver 解析为具体物品。
 */
public record GuiIcon(
        String kind,                 // "vanilla" | "itemsadder" | "head" | "custom"
        String key,                  // "minecraft:DIAMOND" or "itemsadder:ns:id"
        Integer amount,
        String name,                 // 支持 placeholders
        List<String> lore,           // 支持 placeholders
        Map<String, Object> meta     // customModelData, nbt patch, etc.
) {
    public static GuiIcon vanilla(String material, String name, List<String> lore) {
        return new GuiIcon("vanilla", material, 1, name, lore, Map.of());
    }

    public static GuiIcon itemsAdder(String iaId, String name, List<String> lore) {
        return new GuiIcon("itemsadder", iaId, 1, name, lore, Map.of());
    }
}