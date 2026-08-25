package api.linlang.audit.problem;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 由稳定代码、上下文和原始 Java 异常组成的问题记录。
 *
 * <p>问题记录不依赖语言文件。代码用于查询含义，上下文用于定位现场，
 * cause 用于保存 Java 异常类型、堆栈和原因链。</p>
 */
public final class LinProblem {

    private final Instant timestamp;
    private final String code;
    private final Map<String, Object> context;
    private final Throwable cause;

    private LinProblem(Builder builder) {
        this.timestamp = builder.timestamp;
        this.code = builder.code;
        this.context = Collections.unmodifiableMap(new LinkedHashMap<>(builder.context));
        this.cause = builder.cause;
    }

    /**
     * 创建问题构建器。
     *
     * @param code 稳定的问题代码
     * @return 构建器
     */
    public static Builder builder(String code) {
        return new Builder(code);
    }

    /**
     * 快速建立一条问题记录。
     *
     * @param code    稳定的问题代码
     * @param cause   原始 Java 异常，可以为 null
     * @param context 上下文字段，按照 key、value 顺序提供
     * @return 问题记录
     */
    public static LinProblem of(String code, Throwable cause, Object... context) {
        Builder builder = builder(code).cause(cause);
        if (context != null) {
            for (int index = 0; index + 1 < context.length; index += 2) {
                builder.context(String.valueOf(context[index]), context[index + 1]);
            }
            if ((context.length & 1) == 1) {
                builder.context("unpaired", context[context.length - 1]);
            }
        }
        return builder.build();
    }

    /**
     * 返回问题发生时间。
     *
     * @return 问题发生时间
     */
    public Instant timestamp() {
        return timestamp;
    }

    /**
     * 返回稳定的问题代码。
     *
     * @return 已规范为大写的问题代码
     */
    public String code() {
        return code;
    }

    /**
     * 返回用于定位现场的只读上下文。
     *
     * @return 上下文字段
     */
    public Map<String, Object> context() {
        return context;
    }

    /**
     * 返回触发问题的原始 Java 异常。
     *
     * @return 原始异常，可以为 null
     */
    public Throwable cause() {
        return cause;
    }

    /**
     * 问题记录构建器。
     */
    public static final class Builder {

        private Instant timestamp = Instant.now();
        private final String code;
        private final Map<String, Object> context = new LinkedHashMap<>();
        private Throwable cause;

        private Builder(String code) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("code");
            }
            this.code = code.trim().toUpperCase(java.util.Locale.ROOT);
        }

        /**
         * 覆盖问题发生时间。
         *
         * @param timestamp 问题发生时间
         * @return 当前构建器
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
            return this;
        }

        /**
         * 添加一项定位上下文。
         *
         * @param key   字段名称
         * @param value 字段值
         * @return 当前构建器
         */
        public Builder context(String key, Object value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("key");
            }
            context.put(key.trim(), value);
            return this;
        }

        /**
         * 保存触发问题的原始 Java 异常。
         *
         * @param cause 原始异常，可以为 null
         * @return 当前构建器
         */
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        /**
         * 建立不可变的问题记录。
         *
         * @return 问题记录
         */
        public LinProblem build() {
            return new LinProblem(this);
        }
    }
}
