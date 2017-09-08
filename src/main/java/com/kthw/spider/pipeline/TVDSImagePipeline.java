package com.kthw.spider.pipeline;

import com.kthw.spider.model.TVDSBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/10/9.
 */
public class TVDSImagePipeline extends FilePersistentBase implements Pipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TVDSImagePipeline.class);

    private Map<String, String> params;

    public TVDSImagePipeline(Map<String, String> params) {
        setPath(params.get("LOCAL_PATH"));
        this.params = params;
    }

    public void process(ResultItems resultItems, Task task) {
        List<TVDSBean> tvDataList = (List<TVDSBean>) resultItems.get("TVDataList");
        if (CollectionUtils.isNotEmpty(tvDataList)) {
            CloseableHttpClient httpClient = null;
            try {
                httpClient = HttpClients.createDefault();
                for (TVDSBean bean : tvDataList) {
                    if (bean.getSFailureWarnId() != null) {
                        HttpGet httpGet = new HttpGet(params.get("IMG_URL") + bean.getSFailureWarnId());
                        LOG.info("download tvds img path is :" + httpGet.toString());
                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        InputStream in = entity.getContent();
                        File file = new File(this.path + PATH_SEPERATOR + bean.getSFailureWarnId() + ".jpg");
                        FileOutputStream out;
                        try {
                            out = new FileOutputStream(file);
                            int l = -1;
                            byte[] tmp = new byte[1024];
                            while ((l = in.read(tmp)) != -1) {
                                out.write(tmp, 0, l);
                            }
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            LOG.error("write " + bean.getSFailureWarnId() + ".jpg error", e);
                        } finally {
                            in.close();
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error(e.toString());
            } finally {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        LOG.error("HttpClient close exception", e);
                    }
                }
            }
        }
    }

}
