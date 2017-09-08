package com.kthw.spider.pipeline;

import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by YFZX-WB on 2016/9/19.
 */
public abstract class KTPipeline implements Pipeline {

    private Map<String, Integer> baseInfo;

    public KTPipeline() {
        this.baseInfo = new HashMap<String, Integer>();
    }

    public Integer queryDevId(String siteName, String devType, Connection conn) {
        Integer devId = baseInfo.get(siteName);
        if (devId == null) {
            PreparedStatement ps = null;
            String sql = "SELECT DEV_ID FROM DEV_BASE_INFO WHERE SITE_NAME LIKE ? AND DEV_TYPE = ?";
            devId = 0;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, siteName + "%");
                ps.setString(2, devType);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    devId = rs.getInt("DEV_ID");
                }
                baseInfo.put(siteName, devId);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return devId;
    }

    public Integer queryBureauId(String bureauName, Connection conn) {
        Integer bureauId = baseInfo.get(bureauName);
        if (bureauId == null) {
            PreparedStatement ps = null;
            String sql = "SELECT BUREAU_ID FROM KT_BUREAU_DICT WHERE BUREAU_ABBR = ? OR BUREAU_NAME = ?";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, bureauName);
                ps.setString(2, bureauName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    bureauId = rs.getInt("BUREAU_ID");
                } else {
                    bureauId = 0;
                }
                baseInfo.put(bureauName, bureauId);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return bureauId;
    }

    public Integer queryTrainType(String trainType, Connection conn) {
        Integer trainTypeId = baseInfo.get(trainType);
        if (trainTypeId == null) {
            PreparedStatement ps = null;
            String sql = "SELECT TRAIN_TYPE_ID FROM KT_TRAIN_TYPE_DICT WHERE TRAIN_TYPE_NAME = ?";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, trainType);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    trainTypeId = rs.getInt("TRAIN_TYPE_ID");
                } else {
                    trainTypeId = 0;
                }
                baseInfo.put(trainType, trainTypeId);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return trainTypeId;
    }

    public Set<String> queryVehIdByType(int carType, Connection conn) {
        Set<String> vehSet = new HashSet<String>();
        PreparedStatement ps = null;
        String sql = "SELECT CAR_ID FROM KT_VEHICLE_OWNER WHERE BUREAU_ID = 7 AND CAR_TYPE = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, carType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vehSet.add(rs.getString("CAR_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return vehSet;
    }

}
