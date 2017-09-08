package com.kthw.spider.common;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by YFZX-WB on 2016/9/9.
 */
public class SpiderProperties {

    public static String getValueByKey(String filePath, String key) {
        Properties props = new Properties();
        String value = null;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            value = props.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Map<String, String> getAllProperties(String filePath) {
        Properties props = new Properties();
        Map<String, String> params = new HashMap<String, String>();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            for (Map.Entry entry : props.entrySet()) {
                params.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static void writeProperties(String filePath, String key, String value) {
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream(filePath);
            props.load(in);
            OutputStream out = new FileOutputStream(filePath);
            props.setProperty(key, value);
            props.store(out, "Update " + key + " value");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeProperties(String filePath, Map<String, String> params) {
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream(filePath);
            props.load(in);
            OutputStream out = new FileOutputStream(filePath);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                props.setProperty(entry.getKey(), entry.getValue());
            }
            props.store(out, "Update time value");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NameValuePair[] getAllPostParams(String filePath) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            NameValuePair[] pairs = new NameValuePair[props.size() - 1];
            int index = 0;
            for (Map.Entry entry : props.entrySet()) {
                if (!entry.getKey().equals("BASE_URL")) {
                    pairs[index++] = new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue());
                }
            }
            return pairs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
