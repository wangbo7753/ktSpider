package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.param.TFDSImageParam;
import org.apache.commons.lang3.StringUtils;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by YFZX-WB on 2016/10/9.
 */
public class TFDSImagePipeline extends FilePersistentBase implements Pipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TFDSImagePipeline.class);

    private DBLink dbLink;

    public TFDSImagePipeline(Map<String, String> params, DBLink dbLink) {
        setPath(params.get("LOCAL_PATH"));
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        TFDSImageParam param = resultItems.get("TFImageParam");
        if (param != null && StringUtils.isNotBlank(param.getImageUrl())) {
            String imgUUID = UUID.randomUUID().toString();
            downloadTFImage(param.getImageUrl(), imgUUID);
            updateTFImage(param.getDetailUrl(), imgUUID);
        }
    }

    private void downloadTFImage(String imageUrl, String imgUUID) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(imageUrl);
            LOG.info("download tfds img path is :" + httpGet.toString());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            File file = new File(this.path + PATH_SEPERATOR + imgUUID + ".jpg");
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
                LOG.error("write " + imgUUID + ".jpg error", e);
            } finally {
                in.close();
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

    public void updateTFImage(String detailUrl, String imgUUID) {
        Connection conn = dbLink.open();
        PreparedStatement ps = null;
        String sql = "UPDATE VEH_FAULT SET C11=?, FAULT_POS=null WHERE DEV_TYPE='F' AND FAULT_POS=?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, "tfds/" + imgUUID);
            ps.setString(2, detailUrl);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            dbLink.close(conn);
        }
    }
}
