package com.kthw.spider.common.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

public class DBLinkDefault implements DBLink {

    private static Logger logger = Logger.getLogger(DBLinkDefault.class);

    private Vector<Connection> connections = new Vector<Connection>(); // 数据库连接

    private DBParam dbParam;

    private int errCnt; // 数据连接打开错误计数

    public void init(DBParam param) {
        this.dbParam = param;
        try {
            Class.forName(dbParam.getDriver());
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
        }
    }

    public synchronized Connection open() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbParam.getUrl(), dbParam.getUser(),
                    dbParam.getPassword());
            connections.add(conn);
            errCnt = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(dbParam.toString());
            errCnt++;
        }
        return conn;
    }

    public synchronized void close(Connection conn) {
        try {
            if (conn != null) {
                connections.remove(conn);
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() {
        StringBuffer info = new StringBuffer();
        info.append("\n");
        info.append("Database");
        info.append(" ");
        info.append(dbParam.getAlias());
        info.append("\n");
        info.append("CurrentConnection");
        info.append(" = ");
        info.append(connections.size());
        info.append("\n");
        return info.substring(0);
    }

    public void shutdown() {
        for (int i = 0; i < connections.size(); i++) {
            Connection conn = connections.get(i);
            try {
                conn.close();
            } catch (SQLException ex) {
            }
        }
        connections.removeAllElements();
    }

    public int errCount() {
        return errCnt;
    }

    public boolean test() {
        Connection conn = open();
        boolean ret;
        if (conn == null)
            ret = false;
        else
            ret = true;
        close(conn);
        return ret;
    }
}
