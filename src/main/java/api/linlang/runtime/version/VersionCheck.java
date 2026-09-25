package api.linlang.runtime.version;

import api.linlang.audit.LinAudit;
import api.linlang.audit.problem.ProblemDefinition;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @hidden
 * 兼容性检查。
 *
 * <p>A 或 B 不同拒绝运行；运行时的 C 低于插件要求时拒绝，高于时警告；
 * D 不同静默兼容。比较不依据版本字符串的字典序。</p>
 */
public final class VersionCheck {
    public static final String PROJECT_URL = "https://jling.me/linlang";
    public static final String INCOMPATIBLE_CODE = "LIN-RUNTIME-VERSION-INCOMPATIBLE";
    public static final String WARNING_CODE = "LIN-RUNTIME-VERSION-MISMATCH";
    public static final String INVALID_CODE = "LIN-RUNTIME-VERSION-INVALID";
    private static volatile boolean compatibleVersionWarnings = true;

    private VersionCheck() {
    }

    /**
     * 设置是否输出兼容版本差异警告。
     *
     * <p>该设置只影响 {@link Status#WARNING}，不会改变兼容性结果，也不会隐藏或放行
     * {@link Status#INCOMPATIBLE} 与 {@link Status#INVALID}。</p>
     *
     * @param enabled 是否输出兼容版本差异警告
     */
    public static void compatibleVersionWarnings(boolean enabled) {
        compatibleVersionWarnings = enabled;
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
                    fallbackMessage(INVALID_CODE, required, installed,
                            "Invalid version metadata; expected A.B.C.D."));
        }
        boolean older = actual.compareTo(expected) < 0;
        boolean differentFamily = expected.a() != actual.a() || expected.b() != actual.b();
        boolean missingFeatureRelease = !differentFamily && actual.c() < expected.c();
        Status status = differentFamily || missingFeatureRelease
                ? Status.INCOMPATIBLE
                : actual.c() > expected.c() ? Status.WARNING : Status.COMPATIBLE;
        String message = switch (status) {
            case COMPATIBLE -> "";
            case WARNING -> fallbackMessage(WARNING_CODE, required, installed,
                    "The Runtime feature version is newer than the plugin API version.");
            case INCOMPATIBLE -> fallbackMessage(INCOMPATIBLE_CODE, required, installed,
                    differentFamily
                            ? "The API and Runtime belong to different A.B compatibility families."
                            : "The Runtime feature version is older than the plugin requirement.");
            case INVALID -> throw new IllegalStateException("Unexpected compatibility status");
        };
        return new Result(status, required, installed, older, message);
    }

    private static String fallbackMessage(String code, String required, String installed, String detail) {
        return "[" + code + "] " + detail + " API=" + required + ", Runtime=" + installed
                + ". " + PROJECT_URL;
    }

    /**
     * 在语言与审计服务尚不可用的启动早期执行检查。
     *
     * <p>不兼容时抛出 Java 异常，运行时 C 较高时向接收器发送英文回退消息。</p>
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
        if (result.warning() && compatibleVersionWarnings) warning.accept(result.message());
        return result;
    }

    /**
     * 使用运行时问题目录中的当前语言文本完成兼容性检查。
     *
     * <p>比较规则仍由本类执行。问题目录不可用或未包含对应代码时，使用不依赖
     * Runtime 的英文回退消息，保证早期启动错误仍然可读。</p>
     *
     * @param required 期望版本
     * @param installed 已安装版本
     * @param audit 已安装运行时提供的审计入口
     * @return 允许运行的比较结果
     * @throws IllegalStateException 版本不兼容或无法识别
     */
    public static Result requireCompatible(String required, String installed, LinAudit audit) {
        Objects.requireNonNull(audit, "audit");
        Result result = check(required, installed);
        if (result.status() == Status.COMPATIBLE) return result;

        ProblemDefinition definition = audit.problem().lookup(result.code()).orElse(null);
        String message = message(result, definition);
        if (!result.allowed()) throw new IllegalStateException(message);
        if (!compatibleVersionWarnings) return result;

        audit.logger().warn(
                message,
                "code", result.code(),
                "required", result.required(),
                "runtime", result.installed()
        );
        return result;
    }

    /**
     * 使用问题定义说明和兼容性上下文组成最终消息。
     *
     * @param result 兼容性结果
     * @param definition 当前语言的问题定义，可以为 null
     * @return 可用于日志或异常的消息；兼容时为空字符串
     */
    public static String message(Result result, ProblemDefinition definition) {
        Objects.requireNonNull(result, "result");
        if (result.status() == Status.COMPATIBLE) return "";
        if (definition == null || !result.code().equalsIgnoreCase(definition.code())) {
            return result.message();
        }

        StringBuilder message = new StringBuilder()
                .append('[').append(result.code()).append("] ")
                .append(definition.description())
                .append(" API=").append(result.required())
                .append(", Runtime=").append(result.installed());
        if (!definition.resolution().isBlank()) {
            message.append(' ').append(definition.resolution());
        }
        return message.toString();
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
     * message 是不依赖 Runtime 与语言目录的英文回退文本。
     */
    public record Result(Status status, String required, String installed, boolean installedOlder, String message) {
        public boolean allowed() {
            return status == Status.COMPATIBLE || status == Status.WARNING;
        }

        public boolean warning() {
            return status == Status.WARNING;
        }

        /**
         * 返回与当前状态对应的稳定 Problem 代码。
         *
         * @return Problem 代码；兼容时为空字符串
         */
        public String code() {
            return switch (status) {
                case COMPATIBLE -> "";
                case WARNING -> WARNING_CODE;
                case INCOMPATIBLE -> INCOMPATIBLE_CODE;
                case INVALID -> INVALID_CODE;
            };
        }
    }

    /**
     * 外部版本比较结果；projectUrl 是获取构建的项目地址。
     */
    public record UpdateResult(boolean updateAvailable, Result compatibility, String projectUrl) {
    }
}
