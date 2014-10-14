package ca.uwaterloo.iss4e.databases;

import ca.uwaterloo.iss4e.common.SMASException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

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
public interface PowerDAO extends DAO {
    JSONArray getAllCustomerIDs() throws SMASException;

    JSONArray getAllAreaTowns() throws SMASException;

    void getSegmentationByBaseActivityLoad(String clusterFields, String distance, int ncluster, JSONObject out) throws SMASException;

    void getSegmentationByDailyAvgLoadProfile(String distance, int ncluster, JSONObject out) throws SMASException;

    void getThreelinesByID(int meterid, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getDataPointsByID(int meterid, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getConsumptionByArea(String areaCode, int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getConsumptionByAll(int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getConsumptionByMeterID(int meterid, int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getAvgConsumption(int timeType, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getAvgConsumption(int timeType, String IDs, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getAvgConsumptionWithinRadius(int meterid, int timeType, double radius, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getAvgHourlyActivityLoad(int meterid, int order, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getWidthHistogram(int homeID, int nbuckets, Date startDate, Date endDate, JSONObject out) throws SMASException;

    void getForcast(int meterid, int forcastLevel, int forcastTime, int timeUnit, JSONObject out) throws SMASException;

    void getCentroids(String measure, JSONObject out) throws SMASException;//Old to be removed!!!
    void getConsumptionCentroids(String measure, JSONObject out) throws SMASException;

    void getClusteredHouseHolds(String measure, JSONObject out) throws SMASException;

    void getGetNeighborhoodWithinRadius(int meterid, double radius, JSONObject out) throws SMASException;
}
