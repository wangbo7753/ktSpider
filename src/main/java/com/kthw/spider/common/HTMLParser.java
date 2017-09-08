package com.kthw.spider.common;

import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * Created by YFZX-WB on 2016/9/19.
 */
public class HTMLParser {

    public static final String TEDS_IMG_REX = "ShowDetail";

    public static String[][] tableParse(List<Selectable> trNodes, int colSize) {
        String[][] table = new String[trNodes.size()][colSize];
        for (int i = 0; i < trNodes.size(); i++) {
            List<Selectable> tdNodes = trNodes.get(i).xpath("//td").nodes();
            int index = 0;
            for (int j = 0; j < colSize; j++) {
                if (table[i][j] == null) {
                    String rowSpan = tdNodes.get(index).xpath("//td/@rowspan").get();
                    String text = tdNodes.get(index).xpath("//td/text()").get();
                    if (StringUtils.isBlank(text)) {
                        text = tdNodes.get(index).xpath("//td/a/text()").get();
                        String onClick = tdNodes.get(index).xpath("//td/@onClick").get();
                        if (StringUtils.isNotBlank(onClick) && onClick.indexOf(TEDS_IMG_REX) != -1) {
                            int start = onClick.lastIndexOf("=") + 1;
                            onClick = onClick.substring(start, start + 36);
                            text += "_" + onClick;
                        }
                    }
                    if (StringUtils.isBlank(rowSpan) || "1".equals(rowSpan)) {
                        table[i][j] = text;
                    } else {
                        int count = Integer.parseInt(rowSpan);
                        for (int k = 0; k < count; k++) {
                            table[i + k][j] = text;
                        }
                    }
                    index++;
                }
            }
        }
        return table;
    }
    
    public static String[][] tableParseEx(List<Selectable> trNodes, int colSize) {
        String[][] table = new String[trNodes.size()][colSize];
        for (int i = 0; i < trNodes.size(); i++) {
            List<Selectable> tdNodes = trNodes.get(i).xpath("//td").nodes();
            int index = 0;
            for (int j = 0; j < colSize; j++) {
                if (table[i][j] == null) {
                    String rowSpan = tdNodes.get(index).xpath("//td/@rowspan").get();
                    String text = tdNodes.get(index).xpath("//td/text()").get();
                    if (StringUtils.isBlank(text)) {
                    	List<Selectable> tdANodes = tdNodes.get(index).xpath("//td/a").nodes();
                    	for(int s = 0; s < tdANodes.size(); s ++){
                    		text += tdNodes.get(index).xpath("//td/a").nodes().get(s).xpath("//a/text()").get();
                    		if(s != tdANodes.size() - 1){
                    			text += ",";
                    		}
                    	}
                        String onClick = tdNodes.get(index).xpath("//td/@onClick").get();
                        if (StringUtils.isNotBlank(onClick) && onClick.indexOf(TEDS_IMG_REX) != -1) {
                            int start = onClick.lastIndexOf("=") + 1;
                            onClick = onClick.substring(start, start + 36);
                            text += "_" + onClick;
                        }
                    }
                    if (StringUtils.isBlank(rowSpan) || "1".equals(rowSpan)) {
                        table[i][j] = text;
                    } else {
                        int count = Integer.parseInt(rowSpan);
                        for (int k = 0; k < count; k++) {
                            table[i + k][j] = text;
                        }
                    }
                    index++;
                }
            }
        }
        return table;
    }

}
