package com.kthw.spider.common.db;

public class DBParam {

    private String alias;
    private String user;
    private String password;
    private String url;
    private String driver;

    public DBParam(String alias, String user, String password, String driver,
                   String url) {
        this.alias = alias;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return alias + ", " + url;
    }
}
