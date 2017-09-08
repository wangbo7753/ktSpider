package com.kthw.spider.main;

import com.kthw.spider.common.db.DBLink;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/13.
 */
public abstract class KTSpider implements Runnable {

    private boolean running = true;

    private DBLink dbLink;

    public KTSpider(DBLink dbLink) {
        this.dbLink = dbLink;
    }

    public abstract void process(Map<String, String> params);

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public DBLink getDbLink() {
        return dbLink;
    }

    public void setDbLink(DBLink dbLink) {
        this.dbLink = dbLink;
    }
}
