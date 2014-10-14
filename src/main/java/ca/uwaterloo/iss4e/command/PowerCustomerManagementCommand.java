package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.Constant;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.Impl.PowerDAOImpl;
import ca.uwaterloo.iss4e.databases.PowerDAO;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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

public class PowerCustomerManagementCommand implements Command{
    private static final Logger log = Logger.getLogger(PowerCustomerManagementCommand.class.getName());


    PowerDAO dao = new PowerDAOImpl();

    public void allCustomerIDs(ServletContext ctx, HttpServletRequest request,
                               HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        Element elem = cache.get(Constant.METERIDS);
        JSONArray customerIDs = new JSONArray();
        if (elem == null) {
            customerIDs = dao.getAllCustomerIDs();
            elem = new Element(Constant.METERIDS, customerIDs);
            cache.put(elem);
        } else {
            customerIDs = (JSONArray) elem.getObjectValue();
        }
        out.put("values", customerIDs);
    }

    public void allAreaTowns(ServletContext ctx, HttpServletRequest request,
                             HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        Element elem = cache.get(Constant.AREA_TOWN);
        JSONArray areaTowns = new JSONArray();
        if (elem == null) {
            areaTowns = dao.getAllAreaTowns();
            elem = new Element(Constant.AREA_TOWN, areaTowns);
            cache.put(elem);
        } else {
            areaTowns = (JSONArray) elem.getObjectValue();
        }
        out.put("values", areaTowns);
    }

}
