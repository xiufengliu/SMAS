package ca.uwaterloo.iss4e.databases;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.dto.Rule;
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
public interface FeedbackDAO {
    int nextID() throws SMASException;
    void read(Rule rule, JSONObject out) throws SMASException;
    void readByRuleID(int ruleID, JSONObject out) throws SMASException;
    void readRuleTypes(JSONObject out) throws SMASException;
    void readFormByRuleType(int typeID, JSONObject out) throws SMASException;
    void create(Rule rule, JSONObject out) throws SMASException;
    void delete(Rule rule, JSONObject out) throws SMASException;
    int switchEnbleFlag(Rule rule, JSONObject out) throws SMASException;
}
