# Linlang API

Linlang（琳琅）是一个适用于 Bukkit/Paper 插件开发的服务框架，提供多种统一服务。

- 文件与语言
- 数据库
- 命令
- 审计与日志
- 容器 GUI

当前仓库为 Linlang API 仓库。要在 Bukkit/Paper Server 中使用 Linlang，您还需要安装 [Linlang Runtime](https://github.com/G-JLING/linlang)。

> 项目仍在开发中。

## 使用

在插件项目中添加 `provided` 依赖：

```xml
<dependency>
    <groupId>me.jling</groupId>
    <artifactId>linlang-api</artifactId>
    <version>2.2.1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

在 `plugin.yml` 中声明 Runtime 依赖：

```yaml
depend:
  - LinlangRuntimeBukkit
```

在插件主类中取得插件级 Linlang 门面：

```java
import api.linlang.runtime.Lin;
import api.linlang.runtime.Linlang;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExamplePlugin extends JavaPlugin {

    private Linlang lin;

    @Override
    public void onEnable() {
        lin = Lin.init(this);
    }

    @Override
    public void onDisable() {
        if (lin != null) {
            lin.close();
        }
    }
}
```

要了解如何使用 Linlang，请访问 [jling.me/linlang](https://jling.me/linlang-wiki)。
