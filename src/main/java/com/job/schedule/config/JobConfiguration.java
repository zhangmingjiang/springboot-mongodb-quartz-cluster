package com.job.schedule.config;


import com.job.schedule.job.MyJobOne;
import org.joda.time.DateTime;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class JobConfiguration {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @PostConstruct
    private void initialize() throws Exception {
        schedulerFactoryBean.getScheduler().addJob(jobDetailMyJobOne(), true, true);
        if (!schedulerFactoryBean.getScheduler().checkExists(new TriggerKey("trigger1", "mygroup"))) {
            schedulerFactoryBean.getScheduler().scheduleJob(cronTriggerMyJobOne());
        }
    }

    private static JobDetail jobDetailMyJobOne() {
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setKey(new JobKey("jobOne", "mygroup"));
        jobDetail.setJobClass(MyJobOne.class);
        jobDetail.setDurability(true);
        JobDataMap map = new JobDataMap();
        map.put("name", "jobOne");
        map.put(MyJobOne.COUNT, 1);
        jobDetail.setJobDataMap(map);
        return jobDetail;
    }

    private static Trigger cronTriggerMyJobOne() {
        return newTrigger()
                .forJob(jobDetailMyJobOne())
                .withIdentity("trigger1", "mygroup")
                .withPriority(100)
                // Job is scheduled for every 1 minute
                .withSchedule(cronSchedule("0 0/1 * 1/1 * ? *"))
                .startAt(DateTime.now().plusSeconds(3).toDate())
                .build();
    }

}
