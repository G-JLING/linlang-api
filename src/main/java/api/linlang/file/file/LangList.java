package api.linlang.file.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 指向语言文件字符串列表字段的稳定引用。
 *
 * <p>解析结果和默认值均为不可修改的列表快照。列表的增删或重排不会替换引用本身。</p>
 */
public interface LangList extends LangValue<List<String>> {

    /**
     * 创建尚未绑定的语言列表引用。
     *
     * @param fallback 缺失键默认值
     * @return 尚未绑定的语言列表引用
     */
    static LangList of(Collection<String> fallback) {
        return new DefaultLangList(fallback);
    }

    /**
     * 创建尚未绑定的语言列表引用。
     *
     * @param fallback 缺失键默认值
     * @return 尚未绑定的语言列表引用
     */
    static LangList of(String... fallback) {
        List<String> values = new ArrayList<>();
        if (fallback != null) Collections.addAll(values, fallback);
        return new DefaultLangList(values);
    }
}

final class DefaultLangList implements LangList {

    private final List<String> fallback;

    DefaultLangList(Collection<String> fallback) {
        List<String> values = new ArrayList<>();
        if (fallback != null) {
            for (String value : fallback) values.add(value == null ? "" : value);
        }
        this.fallback = Collections.unmodifiableList(values);
    }

    @Override
    public String key() {
        return "";
    }

    @Override
    public List<String> fallback() {
        return fallback;
    }

    @Override
    public List<String> resolve() {
        return fallback;
    }

    @Override
    public List<String> resolve(String locale) {
        return fallback;
    }

    @Override
    public String toString() {
        return fallback.toString();
    }
}
