package api.linlang.tool;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 在线玩家查询与权限判断工具。
 */
public final class LinPlayers {

    private LinPlayers() {
    }

    /**
     * 按完整名称查找在线玩家。
     *
     * @param name 玩家名称
     * @return 在线玩家；不存在时为空
     */
    public static Optional<Player> findOnline(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        String expected = name.trim();
        Player exact = Bukkit.getPlayerExact(expected);
        if (exact != null && exact.isOnline()) return Optional.of(exact);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline() && player.getName().equalsIgnoreCase(expected)) return Optional.of(player);
        }
        return Optional.empty();
    }

    /**
     * 按 UUID 查找在线玩家。
     *
     * @param id 玩家 UUID
     * @return 在线玩家；不存在时为空
     */
    public static Optional<Player> findOnline(UUID id) {
        if (id == null) return Optional.empty();
        Player player = Bukkit.getPlayer(id);
        return player != null && player.isOnline() ? Optional.of(player) : Optional.empty();
    }

    /**
     * 返回全部在线玩家的快照。
     *
     * @return 按服务器玩家顺序排列的不可修改快照
     */
    public static List<Player> online() {
        return List.copyOf(Bukkit.getOnlinePlayers());
    }

    /**
     * 按名称前缀搜索在线玩家。
     *
     * @param prefix 名称前缀
     * @return 按名称排序的匹配玩家快照
     */
    public static List<Player> searchOnline(String prefix) {
        if (prefix == null || prefix.isBlank()) return List.of();
        String expected = prefix.trim().toLowerCase(java.util.Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player.class::cast)
                .filter(Player::isOnline)
                .filter(player -> player.getName().toLowerCase(java.util.Locale.ROOT).startsWith(expected))
                .sorted(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * 查找指定坐标一定半径内的在线玩家。
     *
     * @param origin 中心坐标
     * @param radius 半径
     * @return 按服务器玩家顺序排列的快照
     */
    public static List<Player> nearby(Location origin, double radius) {
        return nearby(origin, radius, null);
    }

    /**
     * 查找指定坐标一定半径内的在线玩家，并排除指定玩家。
     *
     * @param origin 中心坐标
     * @param radius 半径
     * @param excluded 需要排除的玩家，可为 {@code null}
     * @return 按服务器玩家顺序排列的快照
     */
    public static List<Player> nearby(Location origin, double radius, Player excluded) {
        if (origin == null || origin.getWorld() == null) return List.of();
        if (!Double.isFinite(radius) || radius < 0) throw new IllegalArgumentException("radius");
        double limit = radius * radius;
        List<Player> result = new ArrayList<>();
        for (Player player : origin.getWorld().getPlayers()) {
            if (player == excluded) continue;
            if (player.isOnline() && player.getLocation().distanceSquared(origin) <= limit) result.add(player);
        }
        return List.copyOf(result);
    }

    /**
     * 查找指定坐标一定半径内最近的在线玩家。
     *
     * @param origin 中心坐标
     * @param radius 半径
     * @return 最近玩家；范围内没有在线玩家时为空
     */
    public static Optional<Player> nearest(Location origin, double radius) {
        return nearest(origin, radius, null);
    }

    /**
     * 查找指定坐标一定半径内最近的在线玩家，并排除指定玩家。
     *
     * @param origin 中心坐标
     * @param radius 半径
     * @param excluded 需要排除的玩家，可为 {@code null}
     * @return 最近玩家；范围内没有在线玩家时为空
     */
    public static Optional<Player> nearest(Location origin, double radius, Player excluded) {
        if (origin == null || origin.getWorld() == null) return Optional.empty();
        Player nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Player player : nearby(origin, radius, excluded)) {
            double distance = player.getLocation().distanceSquared(origin);
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        return Optional.ofNullable(nearest);
    }

    /**
     * 判断玩家是否拥有给出的全部权限。
     *
     * @param player 玩家
     * @param permissions 权限节点
     * @return 所有非空权限均满足时为 {@code true}
     */
    public static boolean hasAllPermissions(Player player, String... permissions) {
        if (player == null) return false;
        if (permissions == null) return true;
        for (String permission : permissions) {
            if (permission != null && !permission.isBlank() && !player.hasPermission(permission.trim())) return false;
        }
        return true;
    }

    /**
     * 判断玩家是否拥有给出的任一权限。
     *
     * @param player 玩家
     * @param permissions 权限节点
     * @return 至少一个非空权限满足时为 {@code true}
     */
    public static boolean hasAnyPermission(Player player, String... permissions) {
        if (player == null || permissions == null) return false;
        for (String permission : permissions) {
            if (permission != null && !permission.isBlank() && player.hasPermission(permission.trim())) return true;
        }
        return false;
    }

    /**
     * 计算指定等级升至下一级所需的完整经验值。
     *
     * @param level 当前等级
     * @return 升至下一级所需经验
     */
    public static long experienceForNextLevel(int level) {
        requireLevel(level);
        if (level <= 15) return 2L * level + 7;
        if (level <= 30) return 5L * level - 38;
        return 9L * level - 158;
    }

    /**
     * 计算恰好到达指定等级所需的累计经验值。
     *
     * @param level 目标等级
     * @return 该等级起点对应的累计经验
     * @throws ArithmeticException 结果超过 {@code long} 范围时
     */
    public static long totalExperienceAtLevel(int level) {
        requireLevel(level);
        long value = level;
        long squared = Math.multiplyExact(value, value);
        if (level <= 16) {
            return Math.addExact(squared, Math.multiplyExact(6, value));
        }
        if (level <= 31) {
            long numerator = Math.addExact(
                    Math.subtractExact(Math.multiplyExact(5, squared), Math.multiplyExact(81, value)),
                    720
            );
            return numerator / 2;
        }
        long numerator = Math.addExact(
                Math.subtractExact(Math.multiplyExact(9, squared), Math.multiplyExact(325, value)),
                4440
        );
        return numerator / 2;
    }

    /**
     * 根据玩家等级和经验条进度计算当前累计经验。
     *
     * <p>该方法不使用可能与等级状态脱节的 {@link Player#getTotalExperience()}。</p>
     *
     * @param player 玩家
     * @return 当前等级状态对应的累计经验
     */
    public static long currentExperience(Player player) {
        Objects.requireNonNull(player, "player");
        int level = player.getLevel();
        requireLevel(level);
        double progress = Math.max(0, Math.min(1, player.getExp()));
        long currentLevelExperience = Math.round(progress * experienceForNextLevel(level));
        return Math.addExact(totalExperienceAtLevel(level), currentLevelExperience);
    }

    /**
     * 计算玩家距离下一级还需要的经验。
     *
     * @param player 玩家
     * @return 需要增加的经验值
     */
    public static long experienceToNextLevel(Player player) {
        Objects.requireNonNull(player, "player");
        long target = totalExperienceAtLevel(Math.addExact(player.getLevel(), 1));
        return Math.subtractExact(target, currentExperience(player));
    }

    /**
     * 计算玩家到达目标等级起点所需的经验差值。
     *
     * <p>正数表示需要增加经验，负数表示需要扣除经验，零表示已经位于目标等级起点。</p>
     *
     * @param player 玩家
     * @param targetLevel 目标等级
     * @return 带方向的经验差值
     */
    public static long experienceToLevel(Player player, int targetLevel) {
        Objects.requireNonNull(player, "player");
        return Math.subtractExact(totalExperienceAtLevel(targetLevel), currentExperience(player));
    }

    private static void requireLevel(int level) {
        if (level < 0) throw new IllegalArgumentException("level");
    }
}
