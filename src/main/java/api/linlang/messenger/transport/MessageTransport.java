package api.linlang.messenger.transport;

/**
 * 消息投递传输层。
 *
 * <p>Bukkit 运行时通过该接口完成本地投递。未来的网络模块可以注册新的实现，
 * 识别远程接收者并把 {@link TransportMessage} 发送到其他服务器。</p>
 */
public interface MessageTransport {

    /**
     * 返回传输层的稳定标识。
     *
     * @return 传输层标识
     */
    String id();

    /**
     * 返回路由优先级。
     *
     * <p>数值较大的传输层优先尝试。</p>
     *
     * @return 路由优先级
     */
    default int priority() {
        return 0;
    }

    /**
     * 判断该传输层是否支持指定接收者。
     *
     * @param recipient 接收者
     * @return 支持时为 {@code true}
     */
    boolean supports(Object recipient);

    /**
     * 投递规范化消息。
     *
     * @param recipient 接收者
     * @param message 规范化消息
     */
    void send(Object recipient, TransportMessage message);
}
