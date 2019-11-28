package com.yinyuan.lotter;

import com.yinyuan.lotter.controller.LotteryController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Application后自动启动
 */
@Slf4j
@Component
public class LotteryApplicationRunner implements ApplicationRunner, Ordered {

    @Autowired
    private LotteryController lotteryController;

    /**
     * 自动启动功能模块
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("LotteryApplicationRunner run");
        lotteryController.analysisLotteryRecord();
    }

    /**
     * 设置顺序
     *
     * @return 执行顺序id
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
