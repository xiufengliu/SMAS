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
public class Rule {
    int ruleID;
    String name;
    int typeID;
    String[]parameters;
    int[] recvIDs;
    String[] recvValues;
    Timestamp nextStartTime;
    int repeatInterval;
    String message;
    int enableFlag;
    int powerFlag;
    int creatorID;
    Timestamp createtime;


    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public int[] getRecvIDs() {
        return recvIDs;
    }

    public void setRecvIDs(int[] recvIDs) {
        this.recvIDs = recvIDs;
    }

    public String[] getRecvValues() {
        return recvValues;
    }

    public void setRecvValues(String[] recvValues) {
        this.recvValues = recvValues;
    }

    public Timestamp getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(Timestamp nextStartTime) {
        this.nextStartTime = nextStartTime;
    }



    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(int enableFlag) {
        this.enableFlag = enableFlag;
    }

    public int getPowerFlag() {
        return powerFlag;
    }

    public void setPowerFlag(int powerFlag) {
        this.powerFlag = powerFlag;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }


}
