package com.yinyuan.lotter;

import com.yinyuan.lotter.excel.SsqStatData;
import com.yinyuan.lotter.model.LotteryRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LotteryAnalysis {

    public static void analysisLotteryRecordList(List<LotteryRecord> list) {
        LotteryRecord record = null;
        int redCount = 0;
        int blueCount = 0;
        HashMap<Integer, Integer> redMap = new HashMap<>();
        for (int i = 1; i <= 33; i++) {
            redMap.put(i, 0);
        }
        HashMap<Integer, Integer> blueMap = new HashMap<>();
        for (int i = 1; i <= 16; i++) {
            blueMap.put(i, 0);
        }
        for (int i = 0; i < list.size(); i++) {
            record = list.get(i);
            String temp = record.getRecord();
            if (list.size() == 100) {
                log.info("当前第" + record.getLotteryNo() + "期，中奖号码为 = " + temp);
            }
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
        redCount = LotteryAnalysis.statistics("红球", redMap, list.size());
        log.info("====== 红球统计结束 ====== ");
        log.info("====== 蓝球统计开始 ====== ");
        blueCount = LotteryAnalysis.statistics("蓝球", blueMap, list.size());
        log.info("====== 蓝球统计结束 ====== ");

        log.info("红球号码总和平均值为 = " + redCount / list.size() + ";蓝球号码总和平均值为 = " + blueCount / list.size());

        int totalCount = redCount + blueCount;
        log.info("最终总和为 = " + totalCount + ";当前平均总和为 = " + totalCount / list.size());

        log.info("==================结束总结==============================");
    }

    /**
     * 分析双色球的Map
     *
     * @param tag  标签
     * @param map  要分析的map
     * @param size 用来确定min的初始值，最大不会超过要分析的数据的长度（最坏情况，每一期都有一个号码中奖）
     * @return
     */
    public static int statistics(String tag, HashMap<Integer, Integer> map, int size) {
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
//            log.info("号码 = " + entry.getKey() + "；出现次数 = " + entry.getValue());
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

    public static SsqStatData covertLotteryDaoToExcelData(LotteryRecord record, String hotNum, String coldNum) {
        SsqStatData data = new SsqStatData();
        data.setLotteryNo(record.getLotteryNo());

        String lotteryRecord = record.getRecord();
        String[] array = lotteryRecord.split(",");
        int redTotal = 0;
        StringBuffer stringBuffer = new StringBuffer();
        List<Integer> red = new LinkedList<>();
        for (int i = 0; i < array.length; i++) {
            int number = Integer.parseInt(array[i]);
            if (i == array.length - 1) {
                data.setBlue(array[i]);
                data.setRed(stringBuffer.toString());
                data.setRedTotal(redTotal);
            } else {
                red.add(number);
                redTotal += number;
                if (i != 0) {
                    stringBuffer.append(",");
                }
                stringBuffer.append(array[i]);
            }
        }

        int lowSection = 0, middleSection = 0, highSection = 0, intervalTotal = 0;
        StringBuffer intervalStringBuffer = new StringBuffer();
        for (int i = 0; i < red.size(); i++) {
            if (red.get(i) <= 11) {
                lowSection++;
            } else if (red.get(i) <= 22) {
                middleSection++;
            } else {
                highSection++;
            }

            if (i + 1 < red.size()) {
                int diff = red.get(i + 1) - red.get(i);
                if (i != 0) {
                    intervalStringBuffer.append("-");
                }
                intervalStringBuffer.append(diff);
                intervalTotal += diff;
            }
        }

        data.setInterval(intervalStringBuffer.toString());
        data.setIntervalTotal(intervalTotal);
        data.setSection(lowSection + "-" + middleSection + "-" + highSection);

        data.setHotNum(hotNum);
        data.setColdNum(coldNum);

        return data;
    }

    public static List<String> assembleNum(List<Integer> input) {
        if (input.size() != 12) {
            return null;
        }

        List<String> result = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(1)).append(",").append(input.get(3)).append(",").append(input.get(4)).append(",").append(input.get(10)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(1)).append(",").append(input.get(4)).append(",").append(input.get(5)).append(",").append(input.get(7)).append(",").append(input.get(8));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(1)).append(",").append(input.get(4)).append(",").append(input.get(6)).append(",").append(input.get(8)).append(",").append(input.get(10));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(1)).append(",").append(input.get(4)).append(",").append(input.get(8)).append(",").append(input.get(9)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(2)).append(",").append(input.get(3)).append(",").append(input.get(4)).append(",").append(input.get(5)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(2)).append(",").append(input.get(3)).append(",").append(input.get(9)).append(",").append(input.get(10)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(1)).append(",").append(input.get(3)).append(",").append(input.get(8)).append(",").append(input.get(10)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(2)).append(",").append(input.get(5)).append(",").append(input.get(6)).append(",").append(input.get(7)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(2)).append(",").append(input.get(5)).append(",").append(input.get(7)).append(",").append(input.get(9)).append(",").append(input.get(10));
        result.add(stringBuffer.toString());

        stringBuffer = new StringBuffer();
        stringBuffer.append(input.get(0)).append(",").append(input.get(3)).append(",").append(input.get(5)).append(",").append(input.get(6)).append(",").append(input.get(9)).append(",").append(input.get(11));
        result.add(stringBuffer.toString());

        return result;
    }
}
