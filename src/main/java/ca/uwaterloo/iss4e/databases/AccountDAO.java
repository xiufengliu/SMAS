package ca.uwaterloo.iss4e.databases;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.dto.Account;
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

public interface AccountDAO {
    void read(int offset, JSONObject out) throws SMASException;

    void create(Account account, JSONObject out) throws SMASException;



    void delete(String[] userIDs, JSONObject out) throws SMASException;


    void update(Account account, JSONObject out) throws SMASException;

    int count(int userID, JSONObject out) throws SMASException;

    void readAccountForEdit(int userID, JSONObject out) throws SMASException;

}
