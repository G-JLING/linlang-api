package api.linlang.tool;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 物品堆栈的无状态判断与计算工具。
 */
public final class LinItems {

    private LinItems() {
    }

    /**
     * 判断物品是否没有可用内容。
     *
     * @param item 物品，可为 {@code null}
     * @return 物品为空、空气或数量不大于零时为 {@code true}
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getType().isAir() || item.getAmount() <= 0;
    }

    /**
     * 创建物品副本。
     *
     * @param item 原物品
     * @return 不共享 ItemMeta 的物品副本
     */
    public static ItemStack copy(ItemStack item) {
        return Objects.requireNonNull(item, "item").clone();
    }

    /**
     * 创建指定数量的物品副本。
     *
     * @param item 原物品
     * @param amount 副本数量
     * @return 指定数量的新物品
     */
    public static ItemStack copyWithAmount(ItemStack item, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount");
        ItemStack result = copy(item);
        result.setAmount(amount);
        return result;
    }

    /**
     * 判断两个物品能否放入同一堆栈。
     *
     * @param left 左侧物品
     * @param right 右侧物品
     * @return 两者均为空，或物品元数据相同时为 {@code true}
     */
    public static boolean canStack(ItemStack left, ItemStack right) {
        if (isEmpty(left) || isEmpty(right)) return isEmpty(left) && isEmpty(right);
        return left.isSimilar(right);
    }

    /**
     * 判断两个非空物品是否使用相同材质。
     *
     * @param left 左侧物品
     * @param right 右侧物品
     * @return 两者均非空且材质相同时为 {@code true}
     */
    public static boolean sameType(ItemStack left, ItemStack right) {
        return !isEmpty(left) && !isEmpty(right) && left.getType() == right.getType();
    }

    /**
     * 判断物品数量是否达到其最大堆叠数量。
     *
     * @param item 物品
     * @return 物品非空且已经满堆时为 {@code true}
     */
    public static boolean isFullStack(ItemStack item) {
        return !isEmpty(item) && item.getAmount() >= item.getMaxStackSize();
    }

    /**
     * 按物品最大堆叠数量拆分原物品。
     *
     * @param item 原物品
     * @return 不共享物品元数据的堆栈副本
     */
    public static List<ItemStack> split(ItemStack item) {
        if (isEmpty(item)) return List.of();
        return split(item, item.getAmount());
    }

    /**
     * 按物品最大堆叠数量拆分指定总量。
     *
     * @param template 物品模板
     * @param amount 待拆分总量
     * @return 不共享物品元数据的堆栈副本
     */
    public static List<ItemStack> split(ItemStack template, int amount) {
        Objects.requireNonNull(template, "template");
        if (isEmpty(template)) return List.of();
        if (amount < 0) throw new IllegalArgumentException("amount");
        if (amount == 0) return List.of();
        int limit = template.getMaxStackSize();
        if (limit <= 0) throw new IllegalArgumentException("Item cannot be stacked");
        List<ItemStack> result = new ArrayList<>((amount - 1) / limit + 1);
        int remaining = amount;
        while (remaining > 0) {
            int part = Math.min(remaining, limit);
            result.add(copyWithAmount(template, part));
            remaining -= part;
        }
        return List.copyOf(result);
    }

    /**
     * 计算目标堆栈还能接收多少个同类物品。
     *
     * @param target 目标堆栈
     * @param incoming 待放入物品
     * @param inventory 所属容器
     * @return 可接收数量；物品不同或目标已满时返回零
     */
    public static int remainingSpace(ItemStack target, ItemStack incoming, Inventory inventory) {
        Objects.requireNonNull(incoming, "incoming");
        Objects.requireNonNull(inventory, "inventory");
        int limit = Math.min(incoming.getMaxStackSize(), inventory.getMaxStackSize());
        if (isEmpty(target)) return limit;
        if (!target.isSimilar(incoming)) return 0;
        return Math.max(0, limit - target.getAmount());
    }

    /**
     * 汇总一组物品中的有效数量。
     *
     * @param items 物品集合
     * @return 非空物品的数量总和
     */
    public static long totalAmount(Iterable<? extends ItemStack> items) {
        Objects.requireNonNull(items, "items");
        long total = 0;
        for (ItemStack item : items) {
            if (!isEmpty(item)) total += item.getAmount();
        }
        return total;
    }

    /**
     * 汇总一组物品中与样本相似的物品数量。
     *
     * @param items 物品集合
     * @param sample 样本物品
     * @return 相似物品的数量总和
     */
    public static long totalAmountSimilar(Iterable<? extends ItemStack> items, ItemStack sample) {
        Objects.requireNonNull(items, "items");
        if (isEmpty(sample)) return 0;
        long total = 0;
        for (ItemStack item : items) {
            if (!isEmpty(item) && item.isSimilar(sample)) total += item.getAmount();
        }
        return total;
    }
}
