package api.linlang.messenger;

/**
 * 标题消息的显示时长，单位为游戏刻。
 *
 * @param fadeIn 淡入时长
 * @param stay 停留时长
 * @param fadeOut 淡出时长
 */
public record TitleTimes(int fadeIn, int stay, int fadeOut) {

    private static final TitleTimes DEFAULT = new TitleTimes(10, 70, 20);

    /**
     * 创建标题时长。
     *
     * @throws IllegalArgumentException 任意时长为负数时
     */
    public TitleTimes {
        if (fadeIn < 0 || stay < 0 || fadeOut < 0) {
            throw new IllegalArgumentException("Title times cannot be negative.");
        }
    }

    /**
     * 返回默认标题时长。
     *
     * @return 10 刻淡入、70 刻停留、20 刻淡出
     */
    public static TitleTimes defaults() {
        return DEFAULT;
    }

    /**
     * 创建标题时长。
     *
     * @param fadeIn 淡入时长
     * @param stay 停留时长
     * @param fadeOut 淡出时长
     * @return 标题时长
     */
    public static TitleTimes of(int fadeIn, int stay, int fadeOut) {
        return new TitleTimes(fadeIn, stay, fadeOut);
    }
}
