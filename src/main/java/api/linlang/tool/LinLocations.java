package api.linlang.tool;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * 世界坐标的无状态转换与比较工具。
 */
public final class LinLocations {

    private LinLocations() {
    }

    /**
     * 返回方块中心坐标。
     *
     * @param block 方块
     * @return 位于方块几何中心的新坐标
     */
    public static Location center(Block block) {
        Objects.requireNonNull(block, "block");
        return block.getLocation().add(0.5, 0.5, 0.5);
    }

    /**
     * 将坐标移动到所在方块的几何中心。
     *
     * @param location 原坐标
     * @return 不修改原坐标的新坐标
     */
    public static Location blockCenter(Location location) {
        Objects.requireNonNull(location, "location");
        Location result = location.clone();
        result.setX(location.getBlockX() + 0.5);
        result.setY(location.getBlockY() + 0.5);
        result.setZ(location.getBlockZ() + 0.5);
        return result;
    }

    /**
     * 判断两个坐标是否位于同一世界。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @return 世界相同时为 {@code true}
     */
    public static boolean sameWorld(Location left, Location right) {
        if (left == null || right == null) return false;
        World a = left.getWorld();
        World b = right.getWorld();
        return a != null && b != null && a.getUID().equals(b.getUID());
    }

    /**
     * 判断两个坐标是否指向同一方块。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @return 世界和方块坐标均相同时为 {@code true}
     */
    public static boolean sameBlock(Location left, Location right) {
        return sameWorld(left, right)
                && left.getBlockX() == right.getBlockX()
                && left.getBlockY() == right.getBlockY()
                && left.getBlockZ() == right.getBlockZ();
    }

    /**
     * 计算同一世界中两个坐标的距离平方。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @return 距离平方
     * @throws IllegalArgumentException 坐标不在同一世界时
     */
    public static double distanceSquared(Location left, Location right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
        if (!sameWorld(left, right)) throw new IllegalArgumentException("Locations are in different worlds");
        return left.distanceSquared(right);
    }

    /**
     * 计算同一世界中两个坐标的直线距离。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @return 直线距离
     * @throws IllegalArgumentException 坐标不在同一世界时
     */
    public static double distance(Location left, Location right) {
        return Math.sqrt(distanceSquared(left, right));
    }

    /**
     * 计算同一世界中两个坐标忽略高度后的水平距离平方。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @return 水平距离平方
     * @throws IllegalArgumentException 坐标不在同一世界时
     */
    public static double horizontalDistanceSquared(Location left, Location right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
        if (!sameWorld(left, right)) throw new IllegalArgumentException("Locations are in different worlds");
        double x = left.getX() - right.getX();
        double z = left.getZ() - right.getZ();
        return x * x + z * z;
    }

    /**
     * 判断两个坐标是否位于给定直线距离内。
     *
     * @param left 左侧坐标
     * @param right 右侧坐标
     * @param radius 最大距离
     * @return 位于同一世界且距离不超过半径时为 {@code true}
     */
    public static boolean within(Location left, Location right, double radius) {
        if (!Double.isFinite(radius) || radius < 0) throw new IllegalArgumentException("radius");
        return sameWorld(left, right) && distanceSquared(left, right) <= radius * radius;
    }

    /**
     * 计算从起点指向目标点的单位方向向量。
     *
     * @param from 起点
     * @param target 目标点
     * @return 单位方向向量；两点重合时返回零向量
     * @throws IllegalArgumentException 坐标不在同一世界时
     */
    public static Vector directionTo(Location from, Location target) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(target, "target");
        if (!sameWorld(from, target)) throw new IllegalArgumentException("Locations are in different worlds");
        Vector direction = target.toVector().subtract(from.toVector());
        return direction.lengthSquared() == 0 ? direction : direction.normalize();
    }

    /**
     * 返回与向量相加后的坐标副本。
     *
     * @param location 原坐标
     * @param vector 位移向量
     * @return 新坐标
     */
    public static Location addCopy(Location location, Vector vector) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(vector, "vector");
        return location.clone().add(vector);
    }

    /**
     * 返回与向量相减后的坐标副本。
     *
     * @param location 原坐标
     * @param vector 位移向量
     * @return 新坐标
     */
    public static Location subtractCopy(Location location, Vector vector) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(vector, "vector");
        return location.clone().subtract(vector);
    }

    /**
     * 返回朝向目标点的坐标副本。
     *
     * <p>两点重合时保留原坐标的朝向。</p>
     *
     * @param location 原坐标
     * @param target 目标点
     * @return 修改朝向后的新坐标
     */
    public static Location lookAtCopy(Location location, Location target) {
        Location result = Objects.requireNonNull(location, "location").clone();
        Vector direction = directionTo(location, target);
        if (direction.lengthSquared() != 0) result.setDirection(direction);
        return result;
    }

    /**
     * 返回坐标所在区块的 X 坐标。
     *
     * @param location 坐标
     * @return 区块 X 坐标
     */
    public static int chunkX(Location location) {
        return Objects.requireNonNull(location, "location").getBlockX() >> 4;
    }

    /**
     * 返回坐标所在区块的 Z 坐标。
     *
     * @param location 坐标
     * @return 区块 Z 坐标
     */
    public static int chunkZ(Location location) {
        return Objects.requireNonNull(location, "location").getBlockZ() >> 4;
    }

    /**
     * 返回坐标所在区块的打包键。
     *
     * @param location 坐标
     * @return 由区块 X、Z 坐标组成的长整数
     */
    public static long chunkKey(Location location) {
        int x = chunkX(location);
        int z = chunkZ(location);
        return ((long) z << 32) | (x & 0xffffffffL);
    }
}
