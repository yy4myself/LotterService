package com.yinyuan.lotter.controller.impl;

import com.alibaba.fastjson.JSON;
import com.yinyuan.lotter.bean.LotteryBean;
import com.yinyuan.lotter.constant.LotteryTypeConstants;
import com.yinyuan.lotter.controller.LotteryController;
import com.yinyuan.lotter.dao.LotteryDao;
import com.yinyuan.lotter.model.LotteryRecord;
import com.yinyuan.lotter.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LotteryController
 */
@Slf4j
@RestController
public class LotteryControllerImpl implements LotteryController {

    //线程池，控制网络请求
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    @Autowired
    private LotteryDao lotteryDao;

    private int number = 16040;

    @Override
    public void requireRecentLotteryRecord(String lotteryId) {
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String url = "http://apis.juhe.cn/lottery/query";
                String params = "lottery_id=" + lotteryId + "&lottery_no=&key=49d77b67013126fcf34c3f679d1547db&lottery_no=" + number;
                number++;
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
                    lotteryRecord.setLotteryNo(Integer.parseInt(resultBean.getLottery_no()));
                    lotteryDao.insert(lotteryRecord);
                } else {//失败
                    log.error("error_code = " + lotteryBean.getError_code() + ";reason = " + lotteryBean.getReason());
                }
            }
        });
    }

    @Override
    public void analysisLotteryRecord() {
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<LotteryRecord> list = lotteryDao.searchAll(LotteryTypeConstants.ssq);
                LotteryRecord record = null;
                int redCount = 0;
                int blueCount = 0;
                HashMap<Integer, Integer> redMap = new HashMap<>();
                HashMap<Integer, Integer> blueMap = new HashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    record = list.get(i);
                    String temp = record.getRecord();
                    log.info("当前第" + record.getLotteryNo() + "期，中奖号码为 = " + temp);
                    String[] array = temp.split(",");
                    for (int j = 0; j < array.length; j++) {
                        int number = Integer.parseInt(array[j]);
                        if (j == array.length - 1) {
                            if (blueMap.get(number) == null) {
                                blueMap.put(number, 1);
                            } else {
                                Integer value = blueMap.get(number);
                                blueMap.put(number, value + 1);
                            }
                        } else {
                            if (redMap.get(number) == null) {
                                redMap.put(number, 1);
                            } else {
                                Integer value = redMap.get(number);
                                redMap.put(number, value + 1);
                            }
                        }
                    }
                }

                log.info("==================开始总结==============================");
                log.info("总计期数 = " + list.size());

                log.info("====== 红球统计开始 ====== ");
                redCount = statistics("红球", redMap, list.size());
                log.info("====== 红球统计结束 ====== ");
                log.info("====== 蓝球统计开始 ====== ");
                blueCount = statistics("蓝球", blueMap, list.size());
                log.info("====== 蓝球统计结束 ====== ");

                log.info("红球号码总和平均值为 = " + redCount / list.size() + ";蓝球号码总和平均值为 = " + blueCount / list.size());

                int totalCount = redCount + blueCount;
                log.info("最终总和为 = " + totalCount + ";当前平均总和为 = " + totalCount / list.size());

                log.info("==================结束总结==============================");
            }
        });
    }

    /**
     * 分析双色球的Map
     *
     * @param tag  标签
     * @param map  要分析的map
     * @param size 用来确定min的初始值，最大不会超过要分析的数据的长度（最坏情况，每一期都有一个号码中奖）
     * @return
     */
    private int statistics(String tag, HashMap<Integer, Integer> map, int size) {
        //该颜色的球的值总和
        int count = 0;
        //出现最多的次数
        int maxNumber = 0;
        //出现最少的次数
        int minNumber = size;
        //出现最多的次数的球的数值列表
        List<Integer> maxList = new LinkedList<>();
        //出现最少的次数的球的数值列表
        List<Integer> minList = new LinkedList<>();

        //遍历Map
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            log.info("号码 = " + entry.getKey() + "；出现次数 = " + entry.getValue());
            count += entry.getKey() * entry.getValue();
            if (entry.getValue() > maxNumber) {
                maxNumber = entry.getValue();
                maxList = new LinkedList<>();
                maxList.add(entry.getKey());
            } else if (entry.getValue() == maxNumber) {
                maxList.add(entry.getKey());
            }

            if (entry.getValue() < minNumber) {
                minNumber = entry.getValue();
                minList = new LinkedList<>();
                minList.add(entry.getKey());
            } else if (entry.getValue() == minNumber) {
                minList.add(entry.getKey());
            }
        }

        String temp = "";
        for (Integer number : maxList) {
            temp = temp + number + ";";
        }
        log.info(tag + "最多出现的次数是 = " + maxNumber + ";对应数字 = " + temp);

        temp = "";
        for (Integer number : minList) {
            temp = temp + number + ";";
        }
        log.info(tag + "最少出现的次数是 = " + minNumber + ";对应数字 = " + temp);
        return count;
    }
}
