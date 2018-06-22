package com.hubpd.uar.common.task;

import com.hubpd.uar.service.GsdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author cpc
 * @create 2018-06-20 17:16
 **/
@Component
public class TaskExecuteStatusScheduler {
    @Autowired
    private GsdataService gsdataService;

    @Scheduled(fixedRate=1000*60*3)
    public void updateTaskFinishStatus() {
        gsdataService.execute();
    }
}
