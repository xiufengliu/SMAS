package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.databases.MessageDAO;
import ca.uwaterloo.iss4e.dto.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
public class MessageDAOImpl implements MessageDAO {

    @Override
    public void read(Message message, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("select A.msgid, " +
                            "B.firstname||' '|| B.lastname AS recvName, " +
                            "C.firstname||' '|| C.lastname AS senderName, " +
                            "A.sendtime, A.label, " +
                            "A.title, " +
                            "A.content " +
                            "FROM smas_message A, smas_user B, smas_user C " +
                            "WHERE A.recvid=? AND A.recvid=B.userid AND A.senderid=C.userid ORDER BY A.sendtime desc") ;
            pstmt.setInt(1, message.getRecvID());
            ResultSet rs = pstmt.executeQuery();
            JSONArray messages = new JSONArray();
            int[] msgCount = new int[3];
            while (rs.next()) {
                JSONObject msg = new JSONObject();
                msg.put("msgID", rs.getInt("msgid"));
                msg.put("recvName", rs.getString("recvname"));
                msg.put("senderName", rs.getString("sendername"));
                msg.put("sendTime", rs.getString("sendtime"));
                int label = rs.getInt("label");
                msg.put("label", label);
                msg.put("labelName", label==0?"inbox":(label==1?"sent":"trash")); // 0: inbox; 1: sent; 2: trash
                ++msgCount[label];
                msg.put("title", rs.getString("title"));
                msg.put("content", rs.getString("content"));
                messages.put(msg);
            }
            out.put("messages", messages);
            out.put("msgIcons", new String[]{"fa fa-inbox", "fa fa-reply", "fa fa-trash-o"});
            out.put("msgIDs", new String[]{"msg-inbox", "msg-sent", "msg-trash"});
            out.put("msgBoxes", new String[]{"Inbox ("+msgCount[0]+")", "Sent ("+msgCount[1]+")", "Trash ("+msgCount[2]+")"});
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void create(Message message, JSONObject out) throws SMASException {

    }

    @Override
    public void delete(Message message, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("DELETE FROM smas_message WHERE msgid=? and recvid=?");
            pstmt.setInt(1, message.getMsgID());
            pstmt.setInt(2, message.getRecvID());
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
    public void update(Message message, JSONObject out) throws SMASException {

    }

    @Override
    public int count(int userID, JSONObject out) throws SMASException {
        Connection dbConn = null;
        int countOfMessage = 0;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT count(1) FROM smas_message WHERE recvid=?");
            pstmt.setInt(1,userID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                countOfMessage = rs.getInt(1);
            }
            pstmt.close();
            return countOfMessage;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


}
