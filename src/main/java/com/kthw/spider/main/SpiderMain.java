package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.common.db.DBLinkDefault;
import com.kthw.spider.common.db.DBParam;
import com.kthw.spider.vehicle.spider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/13.
 */
public class SpiderMain {

    private static final Logger LOG = LoggerFactory.getLogger(SpiderMain.class);

    private static final String FILE_PATH = "jdbc.properties";

    public static void main(String args[]) {
        LOG.info("Spider data from 5T begin, input 'exit' to exit...");
        SpiderMain main = new SpiderMain();
        DBLink dbLink = main.initLocalDB();
        List<KTSpider> spiders = main.initAllSpider(dbLink);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = null;
        do {
            try {
                cmd = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!"exit".equals(cmd));
        for (KTSpider spider : spiders) {
            spider.setRunning(false);
        }
        LOG.info("Spider while be end...");
    }

    public DBLink initLocalDB() {
        Map<String, String> param = SpiderProperties.getAllProperties(FILE_PATH);
        DBParam dbParam = new DBParam("OracleLocal", param.get("jdbc.username"),
                param.get("jdbc.password"), param.get("jdbc.driverClassName"), param.get("jdbc.url"));
        DBLink dbLink = new DBLinkDefault();
        dbLink.init(dbParam);
        return dbLink;
    }

    public List<KTSpider> initAllSpider(DBLink dbLink) {
        List<KTSpider> spiders = new ArrayList<KTSpider>();
//        spiders.add(new TFDSpider(dbLink));
//        spiders.add(new TEDSpider(dbLink));
//        spiders.add(new TPDSpider(dbLink));
//        spiders.add(new TADSpider(dbLink));
//        spiders.add(new TVDSpider(dbLink));
//        spiders.add(new TCDSpider(dbLink));
//        spiders.add(new VEDSpider(dbLink));
//        spiders.add(new VVDSpider(dbLink));
//        spiders.add(new VFDSpider(dbLink));
//        spiders.add(new VADSpider(dbLink));
//        spiders.add(new VPDSpider(dbLink));
        spiders.add(new DEVSpider(dbLink));
        for (KTSpider spider : spiders) {
            Thread thread = new Thread(spider);
            thread.start();
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                LOG.error("In SpiderMain : ", e);
            }
        }
        return spiders;
    }

}
