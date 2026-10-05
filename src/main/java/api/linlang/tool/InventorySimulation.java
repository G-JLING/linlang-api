package api.linlang.tool;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * 一次容器装入模拟的结果。
 *
 * @param requestedAmount 请求装入的物品总量
 * @param acceptedAmount 能够装入的物品总量
 * @param leftovers 无法装入的物品副本
 */
public record InventorySimulation(
        long requestedAmount,
        long acceptedAmount,
        List<ItemStack> leftovers
) {

    /**
     * 创建不可变的装入模拟结果。
     */
    public InventorySimulation {
        if (requestedAmount < 0) throw new IllegalArgumentException("requestedAmount");
        if (acceptedAmount < 0 || acceptedAmount > requestedAmount) {
            throw new IllegalArgumentException("acceptedAmount");
        }
        Objects.requireNonNull(leftovers, "leftovers");
        leftovers = leftovers.stream()
                .filter(item -> !LinItems.isEmpty(item))
                .map(LinItems::copy)
                .toList();
        if (LinItems.totalAmount(leftovers) != requestedAmount - acceptedAmount) {
            throw new IllegalArgumentException("leftovers");
        }
    }

    /**
     * 返回未能装入的物品总量。
     *
     * @return 未装入数量
     */
    public long rejectedAmount() {
        return requestedAmount - acceptedAmount;
    }

    /**
     * 返回无法装入的物品副本。
     *
     * @return 不可修改且不共享物品实例的列表
     */
    @Override
    public List<ItemStack> leftovers() {
        return leftovers.stream().map(LinItems::copy).toList();
    }

    /**
     * 判断全部物品是否均可装入。
     *
     * @return 没有剩余物品时为 {@code true}
     */
    public boolean fitsCompletely() {
        return acceptedAmount == requestedAmount;
    }
}
