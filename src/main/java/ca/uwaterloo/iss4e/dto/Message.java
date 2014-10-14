package ca.uwaterloo.iss4e.dto;

import java.sql.Timestamp;

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
public class Message {
    int msgID;
    int ruleID;
    int recvID;
    String recvName;
    int senderID;
    String senderName;
    Timestamp sendTime;
    int label;
    String labelName;
    String title;
    String content;

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public int getRecvID() {
        return recvID;
    }

    public void setRecvID(int recvID) {
        this.recvID = recvID;
    }

    public String getRecvName() {
        return recvName;
    }

    public void setRecvName(String recvName) {
        this.recvName = recvName;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgID=" + msgID +
                ", ruleID=" + ruleID +
                ", recvID=" + recvID +
                ", recvName='" + recvName + '\'' +
                ", senderID=" + senderID +
                ", senderName='" + senderName + '\'' +
                ", sendTime=" + sendTime +
                ", label=" + label +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
