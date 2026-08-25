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

    public Instant timestamp() {
        return timestamp;
    }

    public String event() {
        return event;
    }

    public String actor() {
        return actor;
    }

    public String action() {
        return action;
    }

    public String resource() {
        return resource;
    }

    public AuditOutcome outcome() {
        return outcome;
    }

    public String correlationId() {
        return correlationId;
    }

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

        public Builder timestamp(Instant timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
            return this;
        }

        public Builder actor(String actor) {
            this.actor = actor;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Builder outcome(AuditOutcome outcome) {
            this.outcome = outcome == null ? AuditOutcome.UNKNOWN : outcome;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder field(String key, Object value) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("key");
            }
            fields.put(key.trim(), value);
            return this;
        }

        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
}
