package com.kthw.spider.model;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TFDSpider;
import org.apache.commons.lang.StringUtils;

import java.util.Random;

/**
 * Created by YFZX-WB on 2016/11/14.
 */
public class TFDSFaultRandom {

    private static final int LEVEL_1 = 1;

    private static final int LEVEL_2 = 2;

    private static final int LEVEL_3 = 3;

    private static Random random = new Random();

    public static int getFaultLevel(String faultName) {
        String keyFaultStr = SpiderProperties.getValueByKey(TFDSpider.FILE_PATH, "KEY_FAULT");
        if (StringUtils.isNotBlank(keyFaultStr)) {
            for (String keyFault : keyFaultStr.split(",")) {
                if (faultName.equals(keyFault)) {
                    return LEVEL_1;
                }
            }
        }
        return random.nextInt(100) < 70 ? LEVEL_3 : LEVEL_2;
    }

    public static void main(String args[]) {
        int total = 100, level2 = 0, level3 = 0;
        for (int i = 0; i < total; i++) {
            int faultLevel = TFDSFaultRandom.getFaultLevel("");
            if (faultLevel == LEVEL_2) {
                level2++;
            } else if (faultLevel == LEVEL_3) {
                level3++;
            }
        }
        System.out.println("level2 is:" + level2/(double)total);
        System.out.println("level3 is:" + level3/(double)total);
    }
}
