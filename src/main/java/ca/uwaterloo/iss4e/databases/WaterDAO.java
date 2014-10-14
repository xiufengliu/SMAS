package ca.uwaterloo.iss4e.databases;

import ca.uwaterloo.iss4e.common.SMASException;
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
public interface WaterDAO extends DAO {

    void getWaterCustomers(JSONObject out, int startID) throws SMASException;

    void getWaterCustomerTypes(JSONObject out) throws SMASException;

    void getWaterLoadProfileByCustType(JSONObject out, String[] typeNames, int[] subTypeIDs, int timeLevel) throws SMASException;

    void getWaterLoadProfileByCust(JSONObject out, int custId, int timeLevel) throws SMASException;

    void getWaterLoadProfile(JSONObject out, int timeLevel) throws SMASException;

    void getWaterDailyAvgLoadByExoVariable(JSONObject out, String exoVar, String[] typeNames, int[] subTypeIDs) throws SMASException;

    void getWaterLoadDisaggregaionByPARX(JSONObject out, String typeName) throws SMASException;
}
