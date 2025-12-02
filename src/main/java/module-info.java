/**
 * <h2>琳琅（Linlang），一个适用于 Minecraft 多平台支持的开发工具。</h2>
 * <p>您正在阅读琳琅开发者接口文档（Linlang API Javadoc）。开发者接口文档是面向熟悉 Java 的专业开发者，帮助其了解该项目开放的代码接口的文档，其不能帮助您学习使用 Java 或编程。如果您想知道如何快速使用琳琅进行开发，请阅读 <a href="https://jling.me/linlang"> 琳琅 </a> 页面。</p>
 *
 * <p>琳琅不希望处理全部开发中可能遇到的需求，所以琳琅不是一个开发框架，而是一个开发工具。
 * 一方面，如果琳琅希望成为一个框架代理所有开发需求，那么琳琅就做不好这些开发需求。
 * 另一方面，开发者们会希望自己处理一些实现代码，如果琳琅做得不够好，或者不能满足全部需求，那么就会出现「食之无味，弃之可惜」的现象，那么索性这一部分代码就交给开发者们来做。</p>
 *
 * <p>作为工具，琳琅可以使得 Minecraft 开发更为流畅。琳琅提供文件、语言、数据库、日志与审计、GUI（基于箱子 GUI 的）等服务，通过琳琅的服务，您可以省去原先实现这些功能的大量繁琐、高重复性与低业务逻辑性代码。</p>
 */
module linlang.api {
    exports api.linlang.runtime;
    exports api.linlang.file;
    exports api.linlang.file.file;
    exports api.linlang.file.file.annotations;
    exports api.linlang.file.file.migrator;
    exports api.linlang.file.file.implement;
    exports api.linlang.file.database.annotations;
    exports api.linlang.file.database.dto;
    exports api.linlang.file.database.repo;
    exports api.linlang.file.database;
    exports api.linlang.command;
    exports api.linlang.messenger;
    exports api.linlang.audit;
    exports api.linlang.banner;
}