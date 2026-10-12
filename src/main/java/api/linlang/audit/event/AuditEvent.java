package api.linlang.audit.event;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 一条结构化审计事件。
 *
 * <p>事件名称应当是稳定的机器可读标识，例如 {@code config.reload}。
 * 展示文本和多语言内容不应作为事件名称。</p>
 */
public final class AuditEvent {

    private final Instant timestamp;
    private final String event;
    private final String actor;
    private final String action;
    private final String resource;
    private final AuditOutcome outcome;
    private final String correlationId;
    private final Map<String, Object> fields;

    private AuditEvent(Builder builder) {
        this.timestamp = builder.timestamp;
        this.event = builder.event;
        this.actor = builder.actor;
        this.action = builder.action;
        this.resource = builder.resource;
        this.outcome = builder.outcome;
        this.correlationId = builder.correlationId;
        this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(builder.fields));
    }

    /**
     * 创建审计事件构建器。
     *
     * @param event 稳定的事件名称
     * @return 构建器
     */
    public static Builder builder(String event) {
        return new Builder(event);
    }

    /**
     * 使用键值对快速创建审计事件。
     *
     * @param event  稳定的事件名称
     * @param fields 事件字段，按照 key、value 顺序提供
     * @return 审计事件
     */
    public static AuditEvent of(String event, Object... fields) {
        Builder builder = builder(event);
        if (fields != null) {
            for (int index = 0; index + 1 < fields.length; index += 2) {
                builder.field(String.valueOf(fields[index]), fields[index + 1]);
            }
            if ((fields.length & 1) == 1) {
                builder.field("unpaired", fields[fields.length - 1]);
            }
        }
        return builder.build();
    }

    /**
     * 返回事件时间。
     *
     * @return 事件时间
     */
    public Instant timestamp() {
        return timestamp;
    }

    /**
     * 返回稳定的事件标识。
     *
     * @return 事件标识
     */
    public String event() {
        return event;
    }

    /**
     * 返回操作者标识。
     *
     * @return 操作者标识；未设置时为 {@code null}
     */
    public String actor() {
        return actor;
    }

    /**
     * 返回操作名称。
     *
     * @return 操作名称；未设置时为 {@code null}
     */
    public String action() {
        return action;
    }

    /**
     * 返回资源标识。
     *
     * @return 资源标识；未设置时为 {@code null}
     */
    public String resource() {
        return resource;
    }

    /**
     * 返回执行结果。
     *
     * @return 执行结果，默认 {@link AuditOutcome#UNKNOWN}
     */
    public AuditOutcome outcome() {
        return outcome;
    }

    /**
     * 返回关联标识。
     *
     * @return 关联标识；未设置时为 {@code null}
     */
    public String correlationId() {
        return correlationId;
    }

    /**
     * 返回扩展字段。
     *
     * @return 不可修改的扩展字段映射
     */
    public Map<String, Object> fields() {
        return fields;
    }

    /**
     * 审计事件构建器。
     */
    public static final class Builder {

        private Instant timestamp = Instant.now();
        private final String event;
        private String actor;
        private String action;
        private String resource;
        private AuditOutcome outcome = AuditOutcome.UNKNOWN;
        private String correlationId;
        private final Map<String, Object> fields = new LinkedHashMap<>();

        private Builder(String event) {
            if (event == null || event.isBlank()) {
                throw new IllegalArgumentException("event");
            }
            this.event = event.trim();
        }

        /**
         * 设置事件发生时间。
         *
         * @param timestamp 事件时间
         * @return 当前构造器
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
            return this;
        }

        /**
         * 设置操作者标识。
         *
         * @param actor 操作者标识
         * @return 当前构造器
         */
        public Builder actor(String actor) {
            this.actor = actor;
            return this;
        }

        /**
         * 设置操作名称。
         *
         * @param action 操作名称
         * @return 当前构造器
         */
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        /**
         * 设置受影响的资源标识。
         *
         * @param resource 资源标识
         * @return 当前构造器
         */
        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        /**
         * 设置执行结果；{@code null} 视为 {@link AuditOutcome#UNKNOWN}。
         *
         * @param outcome 执行结果
         * @return 当前构造器
         */
        public Builder outcome(AuditOutcome outcome) {
            this.outcome = outcome == null ? AuditOutcome.UNKNOWN : outcome;
            return this;
        }

        /**
         * 设置跨事件关联标识。
         *
         * @param correlationId 关联标识
         * @return 当前构造器
         */
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        /**
         * 添加或覆盖一个扩展字段。
         *
         * @param key 非空白字段名
         * @param value 字段值
         * @return 当前构造器
         * @throws IllegalArgumentException 字段名为空白时
         */
        public Builder field(String key, Object value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("key");
            }
            fields.put(key.trim(), value);
            return this;
        }

        /**
         * 创建不可变审计事件。
         *
         * @return 审计事件
         */
        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
}
