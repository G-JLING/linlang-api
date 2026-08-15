package api.linlang.view.session;

/**
 * 当前会话中的动态区集合。
 */
public interface DynamicAreas {
    /**
     * 获取指定动态区。
     *
     * @param areaId 动态区 ID
     * @return 动态区；不存在时为 {@code null}
     */
    DynamicAreaView area(String areaId);
}
