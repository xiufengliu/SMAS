package ca.uwaterloo.iss4e.command;

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
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.FeedbackDAO;
import ca.uwaterloo.iss4e.databases.Impl.FeedbackDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.UserDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.schedule.ScheduleDAOImpl;
import ca.uwaterloo.iss4e.databases.ScheduleDAO;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.dto.Rule;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.ee.servlet.QuartzInitializerServlet;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by xiliu on 29/05/14.
 */
public class FeedbackCommand implements Command {
    private static final Logger log = Logger.getLogger(FeedbackCommand.class.getName());
    FeedbackDAO feedbackDao = new FeedbackDAOImpl();
    UserDAO userDao = new UserDAOImpl();
    ScheduleDAO scheduleDAO = new ScheduleDAOImpl();

    public void read(ServletContext ctx, HttpServletRequest request,
                     HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        Rule rule = new Rule();
        rule.setCreatorID(userInfo.getUserid());
        feedbackDao.read(rule, out);
        feedbackDao.readRuleTypes(out);
        userDao.readUsernames(out);
    }

    public void readForm(ServletContext ctx, HttpServletRequest request,
                         HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int typeID = Integer.parseInt(request.getParameter("typeID"));
        if (typeID > 0) {
            feedbackDao.readFormByRuleType(typeID, out);
        } else {
            out.put("formtemplate", "");
        }
    }

    public void create(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        String ruleName = request.getParameter("ruleName");
        int typeID = Integer.parseInt(request.getParameter("typeID"));
        String paramStr = request.getParameter("params");
        String recvIDStr = request.getParameter("recvIDs");
        String recvValueStr = request.getParameter("recvValues");
        String message = request.getParameter("message");
        String nextStartTime = request.getParameter("nextStartTime");
        int repeatInterval = Integer.parseInt(request.getParameter("repeatInterval"));
        int powerFlag = Integer.parseInt(request.getParameter("powerFlag"));
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");

        Rule rule = new Rule();
        rule.setName(ruleName);
        rule.setTypeID(typeID);
        rule.setParameters(paramStr.split(";"));
        String[] userIDArr = recvIDStr.split(";");
        int[] recvIDs = new int[userIDArr.length];
        for (int i = 0; i < userIDArr.length; ++i) {
            recvIDs[i] = Integer.parseInt(userIDArr[i]);
        }
        rule.setRecvIDs(recvIDs);
        rule.setRecvValues(recvValueStr.split(";"));
        rule.setMessage(message);
        rule.setPowerFlag(powerFlag);
        rule.setNextStartTime(Utils.toTimestamp(nextStartTime));
        rule.setRepeatInterval(repeatInterval);
        rule.setCreatorID(userInfo.getUserid());

        feedbackDao.create(rule, out);
    }

    public void delete(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int ruleID = Integer.parseInt(request.getParameter("ruleID"));
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        Rule rule = new Rule();
        rule.setRuleID(ruleID);
        rule.setCreatorID(userInfo.getUserid());
        try {
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            JobKey jobKey = new JobKey("Job-" + ruleID, Scheduler.DEFAULT_GROUP);
            TriggerKey triggerKey = new TriggerKey("Trigger-" + ruleID, Scheduler.DEFAULT_GROUP);
            if (scheduler.checkExists(triggerKey))
                scheduler.unscheduleJob(triggerKey);
            if (scheduler.checkExists(jobKey))
                scheduler.deleteJob(jobKey);
            log.info("Trigger-" + ruleID + ", Job-" + ruleID + " are deleted!");
        } catch (SchedulerException e) {
            throw new SMASException(e);
        }
        feedbackDao.delete(rule, out);
    }

    public void update(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int ruleID = Integer.parseInt(request.getParameter("ruleID"));
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        Rule rule = new Rule();
        rule.setRuleID(ruleID);
        rule.setCreatorID(userInfo.getUserid());
        boolean isEnabled = (feedbackDao.switchEnbleFlag(rule, out) == 1);
        try {
            StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = factory.getScheduler();
            if (scheduler.isStarted()) {
                JobKey jobKey = new JobKey("Job-" + ruleID, Scheduler.DEFAULT_GROUP);
                TriggerKey triggerKey = new TriggerKey("Trigger-" + ruleID, Scheduler.DEFAULT_GROUP);
                boolean isExisted = scheduler.checkExists(triggerKey) || scheduler.checkExists(jobKey);
                if (isEnabled && !isExisted) {
                    Map<JobDetail, Set<? extends Trigger>> jobProfiles = scheduleDAO.createJobForRule(rule);
                    if (jobProfiles.size() > 0) {
                        scheduler.scheduleJobs(jobProfiles, true);
                        log.info("Trigger-" + ruleID + ", Job-" + ruleID + " are created!");
                    }
                }

                if (!isEnabled && isExisted) {
                    if (scheduler.checkExists(triggerKey))
                        scheduler.unscheduleJob(triggerKey);
                    if (scheduler.checkExists(jobKey))
                        scheduler.deleteJob(jobKey);
                    log.info("Trigger-" + ruleID + ", Job-" + ruleID + " are removed!");
                }
            }
        } catch (SchedulerException e) {
            throw new SMASException(e);
        }
    }
}
