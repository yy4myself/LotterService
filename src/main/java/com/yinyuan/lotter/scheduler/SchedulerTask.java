package com.yinyuan.lotter.scheduler;

import com.yinyuan.lotter.controller.LotteryController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 */
@Slf4j
@Component
public class SchedulerTask {

    /**
     * 自动注入lotteryController
     */
    @Autowired
    private LotteryController lotteryController;

    /**
     * 服务开启1s后首次执行，然后每24小时执行一次
     */
    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedRate = 24 * 60 * 60 * 1000)
    private void process() {
        log.info("执行定时任务");
        lotteryController.quireLotteryRecord("ssq");
    }
}
