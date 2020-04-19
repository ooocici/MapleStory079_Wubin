package server.custom.bossrank10;

import client.MapleCharacter;
import database.DBConPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZEV1
 */
public class BossRankManager10 {

    private static class InstanceHolder {

        public static final BossRankManager10 instance = new BossRankManager10();
    }

    public static BossRankManager10 getInstance() {
        return InstanceHolder.instance;
    }

    private BossRankManager10() {
    }

    public Map<String, BossRankInfo10> getInfoMap(int cid) {
        Map<String, BossRankInfo10> info_map = new HashMap<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con1 = DBConPool.getInstance().getDataSource().getConnection()) {
            ps = con1.prepareStatement("select * from divine where cid = ?");
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            while (rs.next()) {
                BossRankInfo10 info = new BossRankInfo10();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
                info_map.put(info.getBossname(), info);
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BossRankManager10.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return info_map;
    }

    public BossRankInfo10 getInfo(int cid, String bossname) {
        BossRankInfo10 info = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con1 = DBConPool.getInstance().getDataSource().getConnection()) {
            ps = con1.prepareStatement("select * from divine where cid = ? and bossname = ?");
            ps.setInt(1, cid);
            ps.setString(2, bossname);
            rs = ps.executeQuery();
            if (rs.next()) {
                info = new BossRankInfo10();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BossRankManager10.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return info;
    }

    public int setLog(int cid, String cname, String bossname, byte type, int update) {
        int ret = -1;
        BossRankInfo10 info = getInfo(cid, bossname);
        boolean add = false;
        boolean doUpdate = true;
        if (info == null) {
            doUpdate = false;
            add = true;
            info = new BossRankInfo10();
            info.setCid(cid);
            info.setCname(cname);
            info.setBossname(bossname);
        }
        switch (type) {
            case 1://積分
                ret = info.getPoints() + update;
                info.setPoints(ret);
                break;
            case 2://次數
                ret = info.getCount() + update;
                info.setCount(ret);
                break;
            default:
                doUpdate = false;
                break;
        }
        if (!doUpdate) {
            if (add) {
                add(info);
            }
            return ret;
        }
        update(info);
        return ret;
    }//zev

    public void update(BossRankInfo10 info) {
        if (info == null) {
            return;
        }

        PreparedStatement ps = null;
        try (Connection con1 = DBConPool.getInstance().getDataSource().getConnection()) {
            ps = con1.prepareStatement("update divine set points = ?,count = ?  where cid = ? and bossname = ?");
            ps.setInt(1, info.getPoints());
            ps.setInt(2, info.getCount());
            ps.setInt(3, info.getCid());
            ps.setString(4, info.getBossname());
            ps.executeUpdate();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        } finally {
            if (ps != null) {

                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(BossRankManager10.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//zev

    public void add(BossRankInfo10 info) {
        if (info == null) {
            return;
        }

        PreparedStatement ps = null;
        try (Connection con1 = DBConPool.getInstance().getDataSource().getConnection()) {
            ps = con1.prepareStatement("insert into divine (cid,cname,bossname,points,count) values (?,?,?,?,?)");
            ps.setInt(1, info.getCid());
            ps.setString(2, info.getCname());
            ps.setString(3, info.getBossname());
            ps.setInt(4, info.getPoints());
            ps.setInt(5, info.getCount());
            ps.executeUpdate();
        } catch (Exception Ex) {
            Ex.printStackTrace();

        } finally {
            if (ps != null) {

                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(BossRankManager10.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//zev

    public List<BossRankInfo10> getRank(String bossname, int type) {
        List<BossRankInfo10> list = new LinkedList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBConPool.getInstance().getDataSource().getConnection()) {
            switch (type) {
                case 1://積分
                    ps = con.prepareStatement("SELECT * FROM divine WHERE bossname = ?  ORDER BY points DESC LIMIT 100");
                    break;
                case 2://次數
                    ps = con.prepareStatement("SELECT * FROM divine WHERE bossname = ?  ORDER BY count DESC LIMIT 100");
                    break;
                default:
                    ps = con.prepareStatement("SELECT * FROM divine WHERE bossname = ?  ORDER BY points DESC LIMIT 100");
                    break;
            }
            ps.setString(1, bossname);
            rs = ps.executeQuery();
            while (rs.next()) {
                BossRankInfo10 info = new BossRankInfo10();
                info.setCid(rs.getInt("cid"));
                info.setCname(rs.getString("cname"));
                info.setBossname(rs.getString("bossname"));
                info.setPoints(rs.getInt("points"));
                info.setCount(rs.getInt("count"));
                list.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BossRankManager10.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }
}