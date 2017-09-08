package com.kthw.spider.common.db;

import java.sql.Connection;

public interface DBLink {

    void init(DBParam param);

    boolean test();

    Connection open();

    void close(Connection conn);

    void shutdown();

    String getInfo();

    int errCount(); // 打开数据库连接错误计数

}
