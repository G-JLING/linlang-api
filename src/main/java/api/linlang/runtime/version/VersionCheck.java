package api.linlang.runtime.version;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 不依赖网络、语言文件和运行时的兼容性检查入口。
 *
 * <p>A 或 B 不同拒绝运行，C 不同警告，D 不同静默兼容。比较不依据版本字符串的字典序。</p>
 */
public final class VersionCheck {
    public static final String PROJECT_URL = "https://jling.me/linlang";
    public static final String INCOMPATIBLE_CODE = "LIN-RUNTIME-VERSION-INCOMPATIBLE";
    public static final String WARNING_CODE = "LIN-RUNTIME-VERSION-MISMATCH";
    public static final String INVALID_CODE = "LIN-RUNTIME-VERSION-INVALID";

    private VersionCheck() {
    }

    /**
     * 检查期望版本与已安装版本，不输出日志，也不修改服务状态。
     *
     * @param required 期望的 API 版本
     * @param installed 已安装的运行时版本
     * @return 兼容性结果；无效版本返回 INVALID
     */
    public static Result check(String required, String installed) {
        final LinVersion expected;
        final LinVersion actual;
        try {
            expected = LinVersion.parse(required);
            actual = LinVersion.parse(installed);
        } catch (IllegalArgumentException exception) {
            return new Result(Status.INVALID, required, installed, false,
                    "[" + INVALID_CODE + "] 无法识别 Linlang 版本，要求 A.B.C.D 格式：期望="
                            + required + "，已安装=" + installed + "。请检查构建版本：" + PROJECT_URL);
        }
        boolean older = actual.compareTo(expected) < 0;
        Status status = expected.a() != actual.a() || expected.b() != actual.b()
                ? Status.INCOMPATIBLE : expected.c() != actual.c() ? Status.WARNING : Status.COMPATIBLE;
        String message = "";
        if (status != Status.COMPATIBLE) {
            String code = status == Status.INCOMPATIBLE ? INCOMPATIBLE_CODE : WARNING_CODE;
            message = "[" + code + "] Linlang 版本检查：期望=" + required + "，已安装=" + installed
                    + (status == Status.INCOMPATIBLE ? "。A 或 B 不同，已拒绝运行。" : "。C 不同，允许继续运行，请确认功能兼容。");
            message += older
                    ? "已安装版本较低，请获取与期望 A.B 匹配的新构建：" + PROJECT_URL
                    : "请更新依赖方或选择与期望 A.B 匹配的运行时：" + PROJECT_URL;
        }
        return new Result(status, required, installed, older, message);
    }

    /**
     * 在资源创建前执行检查；不兼容时抛出 Java 异常，C 不同时向指定接收器发送警告。
     *
     * @param required 期望版本
     * @param installed 已安装版本
     * @param warning 警告接收器
     * @return 允许运行的比较结果
     * @throws IllegalStateException 版本不兼容或无法识别
     */
    public static Result requireCompatible(String required, String installed, Consumer<String> warning) {
        Objects.requireNonNull(warning, "warning");
        Result result = check(required, installed);
        if (!result.allowed()) throw new IllegalStateException(result.message());
        if (result.warning()) warning.accept(result.message());
        return result;
    }

    /**
     * 比较本地版本和外部提供的最新版本，供后续网络模块调用；本方法不联网、不记录日志。
     *
     * <p>仅 D 增加也属于可用更新，但不会成为兼容性警告。跨 A.B 的新版本会标记为不兼容更新。</p>
     *
     * @param local 本地版本
     * @param latest 外部获得的版本
     * @return 更新与兼容性结果
     * @throws IllegalArgumentException 任一版本无法识别
     */
    public static UpdateResult checkUpdate(String local, String latest) {
        boolean newer = LinVersion.parse(latest).compareTo(LinVersion.parse(local)) > 0;
        return new UpdateResult(newer, check(local, latest), PROJECT_URL);
    }

    /**
     * 检查状态。
     */
    public enum Status {
        COMPATIBLE, WARNING, INCOMPATIBLE, INVALID
    }

    /**
     * 不可变的兼容性结果；installedOlder 只表示数字版本先后，不决定是否允许运行。
     */
    public record Result(Status status, String required, String installed, boolean installedOlder, String message) {
        public boolean allowed() {
            return status == Status.COMPATIBLE || status == Status.WARNING;
        }

        public boolean warning() {
            return status == Status.WARNING;
        }
    }

    /**
     * 外部版本比较结果；projectUrl 是获取构建的项目地址。
     */
    public record UpdateResult(boolean updateAvailable, Result compatibility, String projectUrl) {
    }
}
