package com.yinyuan.lotter.controller;

import com.alibaba.fastjson.JSON;
import com.yinyuan.lotter.bean.LotteryBean;
import com.yinyuan.lotter.dao.LotteryDao;
import com.yinyuan.lotter.model.LotteryRecord;
import com.yinyuan.lotter.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LotteryController
 */
@Slf4j
@RestController
public class LotteryController {

    //线程池，控制网络请求
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    @Autowired
    LotteryDao lotteryDao;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getTest() {
        log.info("test");
        return "hello world!";
    }


    /**
     * 获取彩票最新一期的结果
     */
    public void quireLotteryRecord(String lottery_id) {

        singleThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                String url = "http://apis.juhe.cn/lottery/query";
                String params = "lottery_id=" + lottery_id + "&lottery_no=&key=49d77b67013126fcf34c3f679d1547db";
                String result = HttpRequestUtil.sendGet(url, params);
                log.info("彩票中奖结果查询 result = " + result);
                LotteryBean lotteryBean = JSON.parseObject(result, LotteryBean.class);
                if (lotteryBean.getError_code() == 0) {//成功
                    LotteryBean.ResultBean resultBean = lotteryBean.getResult();
                    LotteryRecord lotteryRecord = new LotteryRecord();
                    //记录类型
                    lotteryRecord.setType(resultBean.getLottery_id());
                    //记录开奖结果
                    lotteryRecord.setRecord(resultBean.getLottery_res());
                    //记录开奖日期
                    lotteryRecord.setDate(resultBean.getLottery_date());
                    //记录兑奖截止日期
                    lotteryRecord.setExDate(resultBean.getLottery_exdate());
                    //记录彩票编号
                    lotteryRecord.setLotteryNo(resultBean.getLottery_no());
                    lotteryDao.insert(lotteryRecord);
                } else {//失败
                    log.error("error_code = " + lotteryBean.getError_code() + ";reason = " + lotteryBean.getReason());
                }
            }
        });
    }
}
