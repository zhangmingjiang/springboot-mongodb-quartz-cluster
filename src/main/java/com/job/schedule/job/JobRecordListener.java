package com.job.schedule.job;

import com.job.schedule.config.QuartzConfiguration;
import com.job.schedule.repository.JobRecordRepository;
import org.quartz.*;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;


/**
 * Listens on quartz Job lifecycle events to save them into a
 * MongoDB history collection, only finished jobs (whether successful or not are saved).
 *
 * @author
 * @see org.quartz.plugins.history.LoggingJobHistoryPlugin
 */
public class JobRecordListener implements SchedulerPlugin, JobListener {


    Logger log = LoggerFactory.getLogger(JobRecordListener.class);


    private String name;
    private Scheduler scheduler;
    private JobRecordRepository repository;

    public void initialize(String pname, Scheduler scheduler, ClassLoadHelper classLoadHelper) throws SchedulerException {
        this.name = pname;
        this.scheduler = scheduler;
        scheduler.getListenerManager().addJobListener(this, EverythingMatcher.allJobs());
    }

    public String getName() {
        return name;
    }

    public void start() {
        // retrieve Spring application context to setup
        try {
            ApplicationContext ctx = (ApplicationContext) scheduler.getContext().get(QuartzConfiguration.CONTEXT_KEY);
            repository = ctx.getBean(JobRecordRepository.class);
        } catch (SchedulerException e) {
            log.error("Unable to retrieve application context from quartz scheduler", e);
        }
    }

    public void shutdown() {
        // nothing to do...
    }

    public void jobToBeExecuted(JobExecutionContext context) {
        // nothing to do...
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("jobWasExecuted :: {}", context);
        if (StringUtils.isEmpty(jobException)) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>() {{
                put("ts", new Date());
                put("name", context.getJobDetail().getKey().getName());
                put("group", context.getJobDetail().getKey().getGroup());
                put("started", context.getFireTime());
                put("runtime", context.getJobRunTime());
                put("refireCount", context.getRefireCount());
                put("result", String.valueOf(context.getResult()));
                put("desc",context.get("desc"));
            }};
            repository.add(hashMap);
            // TODO: have explict field: hasException: true / false ?
        } else {
            repository.add(new HashMap<String, Object>() {{
                put("ts", new Date());
                put("name", context.getJobDetail().getKey().getName());
                put("group", context.getJobDetail().getKey().getGroup());
                put("started", context.getFireTime());
                put("runtime", context.getJobRunTime());
                put("refireCount", context.getRefireCount());
                put("errMsg", jobException.getMessage());
                put("desc",context.get("desc"));
                put("jobException", jobException.getMessage());
            }});
        }
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("jobExecutionVetoed :: {}", context);
        repository.add(new HashMap<String, Object>() {{
            put("ts", new Date());
            put("name", context.getJobDetail().getKey().getName());
            put("group", context.getJobDetail().getKey().getGroup());
            put("started", context.getFireTime());
            put("runtime", context.getJobRunTime());
            put("refireCount", context.getRefireCount());
            put("desc",context.get("desc"));
            put("veto", true);
        }});
    }

}
