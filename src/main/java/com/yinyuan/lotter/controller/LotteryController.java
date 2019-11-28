package com.yinyuan.lotter.controller;

public interface LotteryController {

    /**
     * 获取彩票最新一期的结果
     */
    void requireRecentLotteryRecord(String lotteryId);

    /**
     * 分析开奖结果
     */
    void analysisLotteryRecord();
}
