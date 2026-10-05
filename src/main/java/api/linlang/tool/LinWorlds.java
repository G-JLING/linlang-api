package api.linlang.tool;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Bukkit 世界查询工具。
 */
public final class LinWorlds {

    private LinWorlds() {
    }

    /**
     * 按名称查找已加载世界。
     *
     * @param name 世界名称
     * @return 世界；未加载时为空
     */
    public static Optional<World> find(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        return Optional.ofNullable(Bukkit.getWorld(name.trim()));
    }

    /**
     * 按 UUID 查找已加载世界。
     *
     * @param id 世界 UUID
     * @return 世界；未加载时为空
     */
    public static Optional<World> find(UUID id) {
        return Optional.ofNullable(id).map(Bukkit::getWorld);
    }

    /**
     * 返回已加载世界；不存在时抛出异常。
     *
     * @param name 世界名称
     * @return 已加载世界
     */
    public static World require(String name) {
        return find(name).orElseThrow(() -> new IllegalArgumentException("World is not loaded: " + name));
    }

    /**
     * 返回已加载世界；不存在时抛出异常。
     *
     * @param id 世界 UUID
     * @return 已加载世界
     */
    public static World require(UUID id) {
        return find(id).orElseThrow(() -> new IllegalArgumentException("World is not loaded: " + id));
    }

    /**
     * 返回服务器中全部已加载世界的快照。
     *
     * @return 不可修改的世界快照
     */
    public static List<World> loaded() {
        return List.copyOf(Bukkit.getWorlds());
    }

    /**
     * 返回全部已加载世界的名称。
     *
     * @return 按服务器世界顺序排列的名称快照
     */
    public static List<String> names() {
        return Bukkit.getWorlds().stream().map(World::getName).toList();
    }

    /**
     * 判断坐标所在区块是否已经加载。
     *
     * @param location 坐标
     * @return 世界存在且区块已加载时为 {@code true}
     */
    public static boolean isChunkLoaded(Location location) {
        if (location == null || location.getWorld() == null) return false;
        return location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    /**
     * 返回世界中的在线玩家快照。
     *
     * @param world 世界
     * @return 不可修改的玩家快照
     */
    public static List<Player> players(World world) {
        return List.copyOf(Objects.requireNonNull(world, "world").getPlayers());
    }

    /**
     * 判断 Y 坐标是否位于世界允许的构建高度内。
     *
     * @param world 世界
     * @param y 方块 Y 坐标
     * @return 大于等于最低高度且小于最高高度时为 {@code true}
     */
    public static boolean isWithinHeight(World world, int y) {
        Objects.requireNonNull(world, "world");
        return y >= world.getMinHeight() && y < world.getMaxHeight();
    }

    /**
     * 判断坐标是否位于所属世界允许的构建高度内。
     *
     * @param location 坐标
     * @return 世界存在且高度有效时为 {@code true}
     */
    public static boolean isWithinHeight(Location location) {
        return location != null
                && location.getWorld() != null
                && isWithinHeight(location.getWorld(), location.getBlockY());
    }

    /**
     * 判断坐标是否位于所属世界的边界内。
     *
     * @param location 坐标
     * @return 世界存在且坐标位于世界边界内时为 {@code true}
     */
    public static boolean isInsideBorder(Location location) {
        return location != null
                && location.getWorld() != null
                && location.getWorld().getWorldBorder().isInside(location);
    }

    /**
     * 判断世界当前是否处于白昼阶段。
     *
     * <p>该方法将世界时间的 0 至 11999 刻视为白昼。日落和日出阶段不属于白昼或夜晚。</p>
     *
     * @param world 世界
     * @return 处于白昼阶段时为 {@code true}
     */
    public static boolean isDay(World world) {
        long time = Objects.requireNonNull(world, "world").getTime();
        return time >= 0 && time < 12000;
    }

    /**
     * 判断世界当前是否处于夜晚阶段。
     *
     * <p>该方法将世界时间的 13000 至 22999 刻视为夜晚。日落和日出阶段不属于白昼或夜晚。</p>
     *
     * @param world 世界
     * @return 处于夜晚阶段时为 {@code true}
     */
    public static boolean isNight(World world) {
        long time = Objects.requireNonNull(world, "world").getTime();
        return time >= 13000 && time < 23000;
    }
}
