package com.job.schedule.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MyJobOne extends QuartzJobBean {

    Logger log = LoggerFactory.getLogger(MyJobOne.class);

    public static final String COUNT = "count";
    public static final String LAST_EXECUTION = "LAST_EXECUTION";

    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();
        int cnt = dataMap.getInt(COUNT);
        JobKey jobKey = ctx.getJobDetail().getKey();
        log.info("{}: times executed {}", jobKey, cnt);
        cnt++;
        dataMap.put(COUNT, cnt);
        dataMap.put(LAST_EXECUTION, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        ctx.setResult("ok");
        ctx.put("desc","This is tag from server 0.");

    }

}