package com.yinyuan.lotter.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 中奖记录，用来存放在数据库中
 */
@Data
@Document(collection = "lottery_record")
public class LotteryRecord {

    /**
     * 开奖日期
     */
    private String date;

    /**
     * 兑奖截止日期
     */
    private String exDate;

    /**
     * 开奖的号码
     */
    private String record;

    /**
     * 彩票类型
     */
    private String type;

    /**
     * 彩票Id
     */
    private String lotteryNo;
}
