package api.linlang.file.file.migrator;

import java.util.*;

/**
 * 供配置迁移器使用的可变树形文档。
 *
 * <p>路径使用点号分隔，例如 {@code database.host}。构造参数不是 Map 时，文档按空映射处理。</p>
 */
public final class MutableDocument {
    private final Map<String, Object> root;

    /**
     * 包装一个树形映射。
     *
     * @param mapLike 根映射；非 Map 值按空文档处理
     */
    @SuppressWarnings("unchecked")
    public MutableDocument(Object mapLike) {
        if (mapLike instanceof Map) {
            this.root = (Map<String, Object>) mapLike;
        } else {
            this.root = new LinkedHashMap<>();
        }
    }

    /**
     * 读取指定路径。
     *
     * @param path 点分隔路径
     * @return 路径值；不存在时为 {@code null}
     */
    public Object get(String path) {
        Node n = dive(path, false);
        return n == null ? null : n.value;
    }

    /**
     * 写入指定路径，并按需建立中间映射。
     *
     * @param path 点分隔路径
     * @param value 新值
     */
    public void set(String path, Object value) {
        Node n = dive(path, true);
        n.parent.put(n.key, value);
    }

    /**
     * 删除指定路径。
     *
     * @param path 点分隔路径
     */
    public void remove(String path) {
        Node n = dive(path, false);
        if (n != null) n.parent.remove(n.key);
    }

    /**
     * 将非空值从一个路径移动到另一个路径。
     *
     * @param from 原路径
     * @param to 目标路径
     */
    public void move(String from, String to) {
        Object v = get(from);
        if (v != null) {
            set(to, v);
            remove(from);
        }
    }

    /**
     * 返回迁移器正在操作的根映射。
     *
     * @return 可变根映射
     */
    public Map<String, Object> unwrap() { return root; }

    // —— 内部 —— //
    private static final class Node { Map<String,Object> parent; String key; Object value; }

    @SuppressWarnings("unchecked")
    private Node dive(String path, boolean create) {
        String[] parts = path.split("\\.");
        Map<String, Object> curr = root;
        for (int i=0;i<parts.length-1;i++){
            Object next = curr.get(parts[i]);
            if (!(next instanceof Map)) {
                if (!create) return null;
                next = new LinkedHashMap<String, Object>();
                curr.put(parts[i], next);
            }
            curr = (Map<String, Object>) next;
        }
        Node n = new Node();
        n.parent = curr;
        n.key = parts[parts.length-1];
        n.value = curr.get(n.key);
        return n;
    }
}
