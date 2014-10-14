package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.Impl.PowerDAOImpl;
import ca.uwaterloo.iss4e.databases.PowerDAO;
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
public class PowerConsumptionPatternCommand implements Command {
    private static final Logger log = Logger.getLogger(PowerLoadAnalysisCommand.class.getName());
    PowerDAO dao = new PowerDAOImpl();

    public void viewCentroid(ServletContext ctx, HttpServletRequest request,
                             HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        String measure = request.getParameter("measure");
        //dao.getCentroids(measure, out);
        dao.getConsumptionCentroids(measure, out);
    }

    public void viewClusteredHouseHold(ServletContext ctx, HttpServletRequest request,
                                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        String measure = request.getParameter("measure");
        dao.getClusteredHouseHolds(measure, out);
    }

}
