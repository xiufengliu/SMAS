package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.Impl.schedule.ScheduleDAOImpl;
import ca.uwaterloo.iss4e.databases.ScheduleDAO;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.ee.servlet.QuartzInitializerServlet;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerKey.triggerKey;

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
public class ScheduleCommand implements Command {
    private static final Logger log = Logger.getLogger(ScheduleCommand.class.getName());


    public void start(ServletContext ctx, HttpServletRequest request,
                      HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            if(scheduler.isShutdown()) {
                scheduler.start();
                //scheduler.scheduleJobs(dao.createJobsForDailyUsageCheckWithNeighbors(), true);
            }
            out.put("status", "Scheduler is started");
        } catch (SchedulerException e) {
            out.put("status", e.getMessage());
            throw new SMASException(e);
        }
    }

    public void restart(ServletContext ctx, HttpServletRequest request,
                      HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            if(scheduler.isStarted()) {
                scheduler.shutdown();
            }
            scheduler.start();
            out.put("status", "Scheduler is re-started");
        } catch (SchedulerException e) {
            out.put("status", e.getMessage());
            throw new SMASException(e);
        }
    }
    public void stop(ServletContext ctx, HttpServletRequest request,
                      HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            scheduler.shutdown();
            out.put("status", "Scheduler is shutdown");
        } catch (SchedulerException e) {
            out.put("status", e.getMessage());
            throw new SMASException(e);
        }
    }

    public void status(ServletContext ctx, HttpServletRequest request,
                     HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            List<JobExecutionContext> ctxes = scheduler.getCurrentlyExecutingJobs();
            JSONArray jobStatus = new JSONArray();
            for (JobExecutionContext jobCtx: ctxes){
                JSONObject job = new JSONObject();
                JobDetail jobDetail = jobCtx.getJobDetail();
                job.put("description", jobDetail.getDescription());
                job.put("fireTime", jobCtx.getFireTime());
                job.put("nextFireTime", jobCtx.getNextFireTime());
                jobStatus.put(job);
            }
            out.put("status", scheduler.isStarted()?"Started":"Not start");
            out.put("jobs",  jobStatus);
        } catch (SchedulerException e) {
            out.put("status", e.getMessage());
            throw new SMASException(e);
        }
    }

}
