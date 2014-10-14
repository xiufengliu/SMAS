package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.databases.FeedbackDAO;
import ca.uwaterloo.iss4e.dto.Rule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Copyright (c) 2014 Xiufeng Liu ( xiufeng.liu@uwaterloo.ca )
 * <p/>
 * This file is free software: you may copy, redistribute and/or modify it
 * under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 * <p/>
 * This file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
public class FeedbackDAOImpl implements FeedbackDAO {
    private static final Logger log = Logger.getLogger(FeedbackDAOImpl.class.getName());

    @Override
    public int nextID() throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT nextval('seq_smas_feedback_rule_ruleid')");
            ResultSet rs = pstmt.executeQuery();
            int ruleid = -1;
            if (rs.next()) {
                ruleid = rs.getInt(1);
            } else {
                throw new SMASException("Failed to get the next serial no!");
            }
            pstmt.close();
            return ruleid;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void readRuleTypes(JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT typeid, name FROM smas_feedback_ruletype ORDER BY 1");
            ResultSet rs = pstmt.executeQuery();
            JSONArray ruleTypes = new JSONArray();
            while (rs.next()) {
                JSONObject ruleType = new JSONObject();
                ruleType.put("typeID", rs.getInt("typeid"));
                ruleType.put("name", rs.getString("name"));
                ruleTypes.put(ruleType);
            }
            out.put("ruleTypes", ruleTypes);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void readFormByRuleType(int typeID, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT formtemplate FROM smas_feedback_ruletype WHERE typeid=?");
            pstmt.setInt(1, typeID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                out.put("formtemplate", rs.getString("formtemplate"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void read(Rule rule, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT ruleid, " +
                            "smas_feedback_rule.name as rulename, " +
                            "smas_feedback_rule.typeid, " +
                            "smas_feedback_ruletype.name as typename," +
                            "parameters," +
                            "recvids, " +
                            "recvvalues," +
                            "nextstarttime, " +
                            "repeatinterval, " +
                            "enableflag " +
                            "FROM smas_feedback_rule, smas_feedback_ruletype " +
                            "WHERE creatorid=? AND smas_feedback_rule.typeid=smas_feedback_ruletype.typeid ORDER BY 1");
            pstmt.setInt(1, rule.getCreatorID());
            ResultSet rs = pstmt.executeQuery();
            JSONArray rules = new JSONArray();
            while (rs.next()) {
                JSONObject rl = new JSONObject();
                rl.put("ruleID", rs.getInt("ruleid"));
                String ruleName = rs.getString("rulename");
                rl.put("ruleName", ruleName);
                rl.put("typeid", rs.getInt("typeid"));
                String[] parameters =  (String[]) (rs.getArray("parameters").getArray());
                String typeName = rs.getString("typename");
                typeName = typeName.replaceAll("__", "%s");
                rl.put("typeName", String.format(typeName, parameters));
                Integer[] recvIDs = (Integer[]) (rs.getArray("recvids").getArray());
                rl.put("recvIDs", Utils.toPrimitiveArray(recvIDs));
                rl.put("recvValues", (String[]) (rs.getArray("recvvalues").getArray()));
                rl.put("nextStartTime", rs.getString("nextstarttime"));
                rl.put("repeatInterval", rs.getInt("repeatInterval"));
                rl.put("enableFlag", rs.getInt("enableflag"));
                rules.put(rl);
            }
            out.put("rules", rules);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void readByRuleID(int ruleID, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT ruleid, " +
                            "smas_feedback_rule.name as rulename, " +
                            "smas_feedback_rule.typeid, " +
                            "smas_feedback_ruletype.name as typename," +
                            "parameters," +
                            "recvids, " +
                            "recvvalues," +
                            "nextstarttime, " +
                            "repeatinterval, " +
                            "enableflag " +
                            "FROM smas_feedback_rule, smas_feedback_ruletype " +
                            "WHERE ruleid=? AND smas_feedback_rule.typeid=smas_feedback_ruletype.typeid ORDER BY 1");
            pstmt.setInt(1, ruleID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JSONObject rl = new JSONObject();
                rl.put("ruleID", rs.getInt("ruleid"));
                String ruleName = rs.getString("rulename");
                rl.put("ruleName", ruleName);
                rl.put("typeid", rs.getInt("typeid"));
                String[] parameters =  (String[]) (rs.getArray("parameters").getArray());
                String typeName = rs.getString("typename");
                typeName = typeName.replaceAll("__", "%s");
                rl.put("typeName", String.format(typeName, parameters));
                Integer[] recvIDs = (Integer[]) (rs.getArray("recvids").getArray());
                rl.put("recvIDs", Utils.toPrimitiveArray(recvIDs));
                rl.put("recvValues", (String[]) (rs.getArray("recvvalues").getArray()));
                rl.put("nextStartTime", rs.getString("nextstarttime"));
                rl.put("repeatInterval", rs.getInt("repeatInterval"));
                rl.put("enableFlag", rs.getInt("enableflag"));
                out.put("rule", rl);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void create(Rule rule, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT nextval('seq_smas_feedback_rule_ruleid')");
            ResultSet rs = pstmt.executeQuery();
            int ruleID = -1;
            if (rs.next()) {
                ruleID = rs.getInt(1);
            } else {
                throw new SMASException("Failed to get the next serial no!");
            }
            pstmt.close();
            pstmt = dbConn
                    .prepareStatement("INSERT INTO smas_feedback_rule(" +
                            "ruleid," +
                            "name," +
                            "typeid," +
                            "parameters," +
                            "recvids," +
                            "recvvalues," +
                            "nextstarttime," +
                            "repeatinterval," +
                            "message," +
                            "enableflag," +
                            "powerflag," +
                            "creatorid," +
                            "createtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)");
            int idx = 0;
            rule.setRuleID(ruleID);
            pstmt.setInt(++idx, rule.getRuleID());
            pstmt.setString(++idx, rule.getName());
            pstmt.setInt(++idx, rule.getTypeID());
            Array paramsArray = dbConn.createArrayOf("varchar", rule.getParameters());
            pstmt.setArray(++idx, paramsArray);
            Array recvIDArray = dbConn.createArrayOf("integer", Utils.toObjectArray(rule.getRecvIDs()));
            pstmt.setArray(++idx, recvIDArray);
            Array recValueArray = dbConn.createArrayOf("varchar", rule.getRecvValues());
            pstmt.setArray(++idx, recValueArray);
            pstmt.setTimestamp(++idx, rule.getNextStartTime());
            pstmt.setInt(++idx, rule.getRepeatInterval());
            pstmt.setString(++idx, rule.getMessage());
            pstmt.setInt(++idx, 0);
            pstmt.setInt(++idx, rule.getPowerFlag());
            pstmt.setInt(++idx, rule.getCreatorID());
            pstmt.execute();
            pstmt.close();
            this.readByRuleID(ruleID, out);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void delete(Rule rule, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("DELETE FROM smas_feedback_rule WHERE ruleid=? and creatorid=?");
            pstmt.setInt(1, rule.getRuleID());
            pstmt.setInt(2, rule.getCreatorID());
            pstmt.execute();
            pstmt.close();
            //    throw new SMASException("Failed to insert the rule!");
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public int switchEnbleFlag(Rule rule, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement updateStmt = dbConn
                    .prepareStatement("UPDATE smas_feedback_rule set enableflag=(1-enableflag) WHERE ruleid=? and creatorid=?");
            updateStmt.setInt(1, rule.getRuleID());
            updateStmt.setInt(2, rule.getCreatorID());
            updateStmt.execute();
            updateStmt.close();
            PreparedStatement selectStmt = dbConn.prepareStatement("SELECT enableflag FROM smas_feedback_rule WHERE ruleid=? and creatorid=?");
            selectStmt.setInt(1, rule.getRuleID());
            selectStmt.setInt(2, rule.getCreatorID());
            ResultSet rs = selectStmt.executeQuery();
            int enableFlag = 0;
            if (rs.next()){
                enableFlag =  rs.getInt(1);
            }
            selectStmt.close();
            return  enableFlag;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }
}
