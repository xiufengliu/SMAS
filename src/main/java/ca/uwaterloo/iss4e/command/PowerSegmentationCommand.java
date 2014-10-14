package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.Impl.PowerDAOImpl;
import ca.uwaterloo.iss4e.databases.PowerDAO;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
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
public class PowerSegmentationCommand implements Command {
    private static final Logger log = Logger.getLogger(PowerSegmentationCommand.class.getName());
    PowerDAO dao = new PowerDAOImpl();
    public void segmentByBaseLoad(ServletContext ctx, HttpServletRequest request,
                                          HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        //Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        //List<Integer> homeIDs = super.getHomeIDs(ctx, request);

       // Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
      //  Date endDate = Utils.toSqlDate(request.getParameter("endDate"));


        String distance = request.getParameter("distance");
        int ncluster = Integer.parseInt(request.getParameter("ncluster"));

        dao.getSegmentationByBaseActivityLoad("baseload", distance, ncluster, out);
        JSONArray aa = new JSONArray();
    }

    public void segmentByActivityLoad(ServletContext ctx, HttpServletRequest request,
                                          HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        //Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        //List<Integer> homeIDs = super.getHomeIDs(ctx, request);

      //  Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
       // Date endDate = Utils.toSqlDate(request.getParameter("endDate"));


        String distance = request.getParameter("distance");
        int ncluster = Integer.parseInt(request.getParameter("ncluster"));

        dao.getSegmentationByBaseActivityLoad("activityload", distance, ncluster, out);
        JSONArray aa = new JSONArray();
    }


    public void segmentByBaseActivityLoad(ServletContext ctx, HttpServletRequest request,
                                          HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        //Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        //List<Integer> homeIDs = super.getHomeIDs(ctx, request);

     //   Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
      //  Date endDate = Utils.toSqlDate(request.getParameter("endDate"));


        String distance = request.getParameter("distance");
        int ncluster = Integer.parseInt(request.getParameter("ncluster"));

        dao.getSegmentationByBaseActivityLoad("baseload,activityload", distance, ncluster, out);
        JSONArray aa = new JSONArray();
    }


    public void segmentByDailyAvgLoadProfile(ServletContext ctx, HttpServletRequest request,
                                               HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        //Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        //List<Integer> homeIDs = super.getHomeIDs(ctx, request);

        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));


        String distance = request.getParameter("distance");
        int ncluster = Integer.parseInt(request.getParameter("ncluster"));

        dao.getSegmentationByDailyAvgLoadProfile(distance, ncluster, out);

    }
}
