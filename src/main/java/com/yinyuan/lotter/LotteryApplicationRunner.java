package com.yinyuan.lotter;

import com.yinyuan.lotter.constant.LotteryTypeConstants;
import com.yinyuan.lotter.dao.LotteryDao;
import com.yinyuan.lotter.model.LotteryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Application后自动启动
 */
@Slf4j
@Component
public class LotteryApplicationRunner implements ApplicationRunner, Ordered {

    @Autowired
    private LotteryDao lotteryDao;

    /**
     * 自动启动功能模块
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("LotteryApplicationRunner run");
        thread.start();
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

    Thread thread = new Thread(new Runnable() {
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

    private int statistics(String tag, HashMap<Integer, Integer> map, int size) {
        int count = 0;
        int maxNumber = 0;
        int minNumber = size;
        List<Integer> maxList = new LinkedList<>();
        List<Integer> minList = new LinkedList<>();

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
