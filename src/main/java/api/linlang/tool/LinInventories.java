package api.linlang.tool;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Bukkit 容器容量与物品数量计算工具。
 */
public final class LinInventories {

    private LinInventories() {
    }

    /**
     * 计算容器还能容纳多少个指定物品。
     *
     * @param inventory 目标容器
     * @param item 指定物品
     * @return 剩余容量
     */
    public static long capacityFor(Inventory inventory, ItemStack item) {
        Objects.requireNonNull(inventory, "inventory");
        if (LinItems.isEmpty(item)) return 0;
        long capacity = 0;
        for (ItemStack current : inventory.getStorageContents()) {
            capacity += LinItems.remainingSpace(current, item, inventory);
        }
        return capacity;
    }

    /**
     * 判断容器能否完整放入指定物品堆栈。
     *
     * @param inventory 目标容器
     * @param item 待放入物品
     * @return 容量足够时为 {@code true}
     */
    public static boolean canFit(Inventory inventory, ItemStack item) {
        if (LinItems.isEmpty(item)) return true;
        return capacityFor(inventory, item) >= item.getAmount();
    }

    /**
     * 判断容器能否同时完整放入全部物品。
     *
     * <p>该方法使用一次连续模拟，多个物品不会重复占用同一空槽位。</p>
     *
     * @param inventory 目标容器
     * @param items 待放入物品
     * @return 全部物品均可放入时为 {@code true}
     */
    public static boolean canFitAll(Inventory inventory, Iterable<? extends ItemStack> items) {
        return simulateAdd(inventory, items).fitsCompletely();
    }

    /**
     * 模拟向容器连续放入一组物品。
     *
     * <p>该方法只操作容器内容的副本，不会修改真实容器或传入的物品。</p>
     *
     * @param inventory 目标容器
     * @param items 待放入物品
     * @return 装入数量和剩余物品
     */
    public static InventorySimulation simulateAdd(
            Inventory inventory,
            Iterable<? extends ItemStack> items
    ) {
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(items, "items");
        ItemStack[] original = inventory.getStorageContents();
        ItemStack[] simulated = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (!LinItems.isEmpty(original[i])) simulated[i] = LinItems.copy(original[i]);
        }

        long requested = 0;
        long accepted = 0;
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack incoming : items) {
            if (LinItems.isEmpty(incoming)) continue;
            int remaining = incoming.getAmount();
            requested = Math.addExact(requested, remaining);

            for (int i = 0; i < simulated.length && remaining > 0; i++) {
                ItemStack current = simulated[i];
                if (LinItems.isEmpty(current) || !current.isSimilar(incoming)) continue;
                int space = LinItems.remainingSpace(current, incoming, inventory);
                int moved = Math.min(space, remaining);
                if (moved <= 0) continue;
                current.setAmount(current.getAmount() + moved);
                remaining -= moved;
            }

            int stackLimit = Math.min(incoming.getMaxStackSize(), inventory.getMaxStackSize());
            for (int i = 0; i < simulated.length && remaining > 0; i++) {
                if (!LinItems.isEmpty(simulated[i])) continue;
                int moved = Math.min(stackLimit, remaining);
                if (moved <= 0) break;
                simulated[i] = LinItems.copyWithAmount(incoming, moved);
                remaining -= moved;
            }

            accepted = Math.addExact(accepted, incoming.getAmount() - remaining);
            if (remaining > 0) leftovers.addAll(LinItems.split(incoming, remaining));
        }
        return new InventorySimulation(requested, accepted, leftovers);
    }

    /**
     * 统计容器中与样本物品相似的物品数量。
     *
     * @param inventory 目标容器
     * @param sample 样本物品
     * @return 相似物品的数量总和
     */
    public static long countSimilar(Inventory inventory, ItemStack sample) {
        Objects.requireNonNull(inventory, "inventory");
        if (LinItems.isEmpty(sample)) return 0;
        long total = 0;
        for (ItemStack current : inventory.getStorageContents()) {
            if (!LinItems.isEmpty(current) && current.isSimilar(sample)) total += current.getAmount();
        }
        return total;
    }

    /**
     * 按条件统计容器储存区域内的物品数量。
     *
     * <p>筛选条件收到的是物品副本，不能借此修改真实容器。</p>
     *
     * @param inventory 目标容器
     * @param filter 物品筛选条件
     * @return 匹配物品的数量总和
     */
    public static long count(Inventory inventory, Predicate<? super ItemStack> filter) {
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(filter, "filter");
        long total = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (LinItems.isEmpty(item)) continue;
            ItemStack snapshot = LinItems.copy(item);
            if (filter.test(snapshot)) total += item.getAmount();
        }
        return total;
    }

    /**
     * 返回储存区域内满足条件的槽位索引。
     *
     * <p>筛选条件收到的是物品副本，不能借此修改真实容器。</p>
     *
     * @param inventory 目标容器
     * @param filter 物品筛选条件
     * @return 不可修改的槽位索引快照
     */
    public static List<Integer> matchingSlots(
            Inventory inventory,
            Predicate<? super ItemStack> filter
    ) {
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(filter, "filter");
        List<Integer> result = new ArrayList<>();
        ItemStack[] contents = inventory.getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            if (LinItems.isEmpty(contents[i])) continue;
            if (filter.test(LinItems.copy(contents[i]))) result.add(i);
        }
        return List.copyOf(result);
    }

    /**
     * 返回容器储存区域的完整副本。
     *
     * <p>返回列表保留空槽位及其索引，非空物品均为独立副本。</p>
     *
     * @param inventory 目标容器
     * @return 不可修改的储存区域快照
     */
    public static List<ItemStack> storageSnapshot(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        ItemStack[] contents = inventory.getStorageContents();
        ItemStack[] snapshot = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            if (!LinItems.isEmpty(contents[i])) snapshot[i] = LinItems.copy(contents[i]);
        }
        return Collections.unmodifiableList(Arrays.asList(snapshot));
    }

    /**
     * 判断容器是否至少持有指定数量的相似物品。
     *
     * @param inventory 目标容器
     * @param sample 样本物品
     * @param amount 最低数量
     * @return 数量满足要求时为 {@code true}
     */
    public static boolean containsAtLeastSimilar(Inventory inventory, ItemStack sample, long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount");
        return countSimilar(inventory, sample) >= amount;
    }

    /**
     * 统计容器中的空储存槽位。
     *
     * @param inventory 目标容器
     * @return 空槽位数量
     */
    public static int emptySlots(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        int count = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (LinItems.isEmpty(item)) count++;
        }
        return count;
    }

    /**
     * 判断容器储存区域是否没有物品。
     *
     * @param inventory 目标容器
     * @return 全部储存槽均为空时为 {@code true}
     */
    public static boolean isEmpty(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        for (ItemStack item : inventory.getStorageContents()) {
            if (!LinItems.isEmpty(item)) return false;
        }
        return true;
    }

    /**
     * 判断容器是否已经无法接收指定物品。
     *
     * @param inventory 目标容器
     * @param item 指定物品
     * @return 非空物品没有剩余容量时为 {@code true}
     */
    public static boolean isFullFor(Inventory inventory, ItemStack item) {
        return !LinItems.isEmpty(item) && capacityFor(inventory, item) == 0;
    }
}
