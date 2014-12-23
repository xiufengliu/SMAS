package ca.uwaterloo.iss4e.dto;

import org.json.JSONObject;

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
public class UserInfo {
    int userid;
    String username;
    String password;
    String firstname;
    String lastname;
    int agender;
    String birthdate;
    String address;
    int roleID;
    int[] powerMeterIDs;
    int[] waterMeterIDs;


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public String getFullname(){
        return String.format("%s %s", this.firstname, this.lastname);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAgender() {
        return agender;
    }

    public void setAgender(int agender) {
        this.agender = agender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public int[] getPowerMeterIDs() {
        return powerMeterIDs;
    }

    public void setPowerMeterIDs(int[] powerMeterIDs) {
        this.powerMeterIDs = powerMeterIDs;
    }

    public int[] getWaterMeterIDs() {
        return waterMeterIDs;
    }

    public void setWaterMeterIDs(int[] waterMeterIDs) {
        this.waterMeterIDs = waterMeterIDs;
    }

    public JSONObject toJsonObject() { // We currently haven't considered the use of watermeters.
        JSONObject obj = new JSONObject();
        obj.put("role", this.roleID);
        int[] meterids = new int[powerMeterIDs.length];
        //int[] meterids = new int[powerMeterIDs.length + waterMeterIDs.length];
        System.arraycopy(powerMeterIDs, 0, meterids, 0, powerMeterIDs.length);
        //System.arraycopy(waterMeterIDs, 0, meterids, powerMeterIDs.length, waterMeterIDs.length);
        obj.put("mymeterids", meterids);
        return obj;
    }
}
