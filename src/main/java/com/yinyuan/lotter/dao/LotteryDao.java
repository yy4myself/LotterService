package com.yinyuan.lotter.dao;

import com.yinyuan.lotter.model.LotteryRecord;

import java.util.List;

/**
 * 数据库操作的接口类
 */
public interface LotteryDao {

    /**
     * 插入
     *
     * @param lotteryRecord 彩票记录
     */
    void insert(LotteryRecord lotteryRecord);

    void delete();

    void update();

    /**
     * 根据开奖日期和类型查询
     *
     * @param date 开奖日期
     * @param type 彩票类型
     * @return 中奖记录
     */
    LotteryRecord search(String date, String type);

    /**
     * 查询所有记录
     *
     * @param type 彩票类型
     * @return 彩票记录
     */
    List<LotteryRecord> searchAll(String type);

    /**
     * 
     * @param type 彩票类型
     * @param num 最近的X期
     * @return
     */
    List<LotteryRecord> searchRecent(String type, int num);
}
