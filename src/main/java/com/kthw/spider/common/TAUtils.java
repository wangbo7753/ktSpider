package com.kthw.spider.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YFZX-WB on 2016/12/27.
 */
public class TAUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TAUtils.class);

    private static final String LOGIN_URL = "http://10.1.184.32/tads/login.aspx";

    public static String getRandomCode() {
        HttpURLConnection httpConn = null;
        String randomCode = null;
        try {
            URL connURL = new URL(LOGIN_URL);
            httpConn = (HttpURLConnection) connURL.openConnection();
            httpConn.connect();
            httpConn.getHeaderFields();
            randomCode = httpConn.getURL().toString();
            if (randomCode != null && randomCode.indexOf("X(1)S(") != -1) {
                randomCode = randomCode.substring(randomCode.indexOf("X(1)") + 4, randomCode.lastIndexOf(")"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpConn.disconnect();
        }
        LOG.info(randomCode);
        return randomCode;
    }

}
