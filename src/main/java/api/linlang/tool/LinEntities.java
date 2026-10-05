package api.linlang.tool;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Bukkit 实体状态与范围查询工具。
 */
public final class LinEntities {

    private LinEntities() {
    }

    /**
     * 判断实体是否仍可在世界中使用。
     *
     * @param entity 实体
     * @return 实体有效且未死亡时为 {@code true}
     */
    public static boolean isAlive(Entity entity) {
        return entity != null && entity.isValid() && !entity.isDead();
    }

    /**
     * 查询球形范围内指定类型的实体。
     *
     * @param origin 中心坐标
     * @param radius 球体半径
     * @param type 实体类型
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearby(Location origin, double radius, Class<T> type) {
        return nearbySphere(origin, radius, type);
    }

    /**
     * 查询立方范围内指定类型的实体。
     *
     * @param origin 中心坐标
     * @param radius 三轴半径
     * @param type 实体类型
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearbyCuboid(Location origin, double radius, Class<T> type) {
        return nearbyCuboid(origin, radius, radius, radius, type);
    }

    /**
     * 查询长方体范围内指定类型的实体。
     *
     * @param origin 中心坐标
     * @param xRadius X 轴半径
     * @param yRadius Y 轴半径
     * @param zRadius Z 轴半径
     * @param type 实体类型
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearbyCuboid(
            Location origin,
            double xRadius,
            double yRadius,
            double zRadius,
            Class<T> type
    ) {
        return nearbyCuboid(origin, xRadius, yRadius, zRadius, type, ignored -> true);
    }

    /**
     * 查询长方体范围内满足条件的指定类型实体。
     *
     * @param origin 中心坐标
     * @param xRadius X 轴半径
     * @param yRadius Y 轴半径
     * @param zRadius Z 轴半径
     * @param type 实体类型
     * @param filter 附加筛选条件
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearbyCuboid(
            Location origin,
            double xRadius,
            double yRadius,
            double zRadius,
            Class<T> type,
            Predicate<? super T> filter
    ) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(filter, "filter");
        if (origin == null || origin.getWorld() == null) return List.of();
        requireRadius(xRadius, "xRadius");
        requireRadius(yRadius, "yRadius");
        requireRadius(zRadius, "zRadius");
        List<T> result = new ArrayList<>();
        for (Entity entity : origin.getWorld().getNearbyEntities(origin, xRadius, yRadius, zRadius)) {
            if (!type.isInstance(entity)) continue;
            T cast = type.cast(entity);
            if (filter.test(cast)) result.add(cast);
        }
        return List.copyOf(result);
    }

    /**
     * 查询球形范围内指定类型的实体。
     *
     * @param origin 中心坐标
     * @param radius 球体半径
     * @param type 实体类型
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearbySphere(Location origin, double radius, Class<T> type) {
        return nearbySphere(origin, radius, type, ignored -> true);
    }

    /**
     * 查询球形范围内满足条件的指定类型实体。
     *
     * @param origin 中心坐标
     * @param radius 球体半径
     * @param type 实体类型
     * @param filter 附加筛选条件
     * @param <T> 实体类型
     * @return 实体快照
     */
    public static <T extends Entity> List<T> nearbySphere(
            Location origin,
            double radius,
            Class<T> type,
            Predicate<? super T> filter
    ) {
        requireRadius(radius, "radius");
        if (origin == null || origin.getWorld() == null) return List.of();
        double limit = radius * radius;
        return nearbyCuboid(origin, radius, radius, radius, type, filter).stream()
                .filter(entity -> entity.getLocation().distanceSquared(origin) <= limit)
                .toList();
    }

    /**
     * 查找球形范围内距离中心最近的指定类型实体。
     *
     * @param origin 中心坐标
     * @param radius 球体半径
     * @param type 实体类型
     * @param <T> 实体类型
     * @return 最近实体；范围内没有匹配实体时为空
     */
    public static <T extends Entity> Optional<T> nearest(Location origin, double radius, Class<T> type) {
        return nearest(origin, radius, type, ignored -> true);
    }

    /**
     * 查找球形范围内距离中心最近且满足条件的指定类型实体。
     *
     * @param origin 中心坐标
     * @param radius 球体半径
     * @param type 实体类型
     * @param filter 附加筛选条件
     * @param <T> 实体类型
     * @return 最近实体；范围内没有匹配实体时为空
     */
    public static <T extends Entity> Optional<T> nearest(
            Location origin,
            double radius,
            Class<T> type,
            Predicate<? super T> filter
    ) {
        if (origin == null || origin.getWorld() == null) return Optional.empty();
        T nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (T entity : nearbySphere(origin, radius, type, filter)) {
            double distance = entity.getLocation().distanceSquared(origin);
            if (distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }
        return Optional.ofNullable(nearest);
    }

    /**
     * 计算两个实体间的距离平方。
     *
     * @param left 左侧实体
     * @param right 右侧实体
     * @return 距离平方
     * @throws IllegalArgumentException 实体不在同一世界时
     */
    public static double distanceSquared(Entity left, Entity right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
        return LinLocations.distanceSquared(left.getLocation(), right.getLocation());
    }

    /**
     * 返回实体所在载具链的最上层实体。
     *
     * @param entity 实体
     * @return 最上层载具；实体未乘坐载具时返回自身
     */
    public static Entity rootVehicle(Entity entity) {
        Entity current = Objects.requireNonNull(entity, "entity");
        Set<Entity> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        visited.add(current);
        Entity vehicle;
        while ((vehicle = current.getVehicle()) != null && visited.add(vehicle)) {
            current = vehicle;
        }
        return current;
    }

    /**
     * 返回实体的全部直接和间接乘客。
     *
     * @param entity 载具实体
     * @return 按层级广度顺序排列的乘客快照
     */
    public static List<Entity> passengersRecursive(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        List<Entity> result = new ArrayList<>();
        Set<Entity> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Entity> pending = new ArrayDeque<>();
        visited.add(entity);
        pending.add(entity);
        while (!pending.isEmpty()) {
            for (Entity passenger : pending.removeFirst().getPassengers()) {
                if (!visited.add(passenger)) continue;
                result.add(passenger);
                pending.addLast(passenger);
            }
        }
        return List.copyOf(result);
    }

    private static void requireRadius(double radius, String name) {
        if (!Double.isFinite(radius) || radius < 0) throw new IllegalArgumentException(name);
    }
}
