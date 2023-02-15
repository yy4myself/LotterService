package com.yinyuan.lotter.controller.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.yinyuan.lotter.LotteryAnalysis;
import com.yinyuan.lotter.bean.LotteryBean;
import com.yinyuan.lotter.constant.LotteryTypeConstants;
import com.yinyuan.lotter.controller.LotteryController;
import com.yinyuan.lotter.dao.LotteryDao;
import com.yinyuan.lotter.excel.SsqStatData;
import com.yinyuan.lotter.model.LotteryRecord;
import com.yinyuan.lotter.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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

    private int pageNum = 1;
    private final static int pageSize = 50;

    @Override
    public void requireRecentLotteryRecord(String lotteryId) {
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String url = "http://apis.juhe.cn/lottery/history";

                String params = "key=49d77b67013126fcf34c3f679d1547db&lottery_id=" + lotteryId + "&page_size=" + pageSize + "&page=" + pageNum;
                String result = HttpRequestUtil.sendGet(url, params);
                log.info("彩票中奖结果查询 result = " + result);
                if (result == null) {
                    log.error("result is null! return");
                    return;
                }

                LotteryBean lotteryBean = JSON.parseObject(result, LotteryBean.class);
                if (lotteryBean.getErrorCode() == 0) {//成功
                    pageNum++;
                    List<LotteryBean.LotteryTermBean> lotteryResList = lotteryBean.getResult().getLotteryResList();
                    for (int i = 0; i < lotteryResList.size(); i++) {
                        LotteryBean.LotteryTermBean lotteryTermBean = lotteryResList.get(i);
                        LotteryRecord lotteryRecord = new LotteryRecord();
                        //记录类型
                        lotteryRecord.setType(lotteryTermBean.getLotteryId());
                        //记录开奖结果
                        lotteryRecord.setRecord(lotteryTermBean.getLotteryRes());
                        //记录开奖日期
                        lotteryRecord.setDate(lotteryTermBean.getLotteryDate());
                        //记录兑奖截止日期
                        lotteryRecord.setExDate(lotteryTermBean.getLotteryExdate());
                        //记录彩票编号
                        lotteryRecord.setLotteryNo(Integer.parseInt(lotteryTermBean.getLotteryNo()));
                        lotteryDao.insert(lotteryRecord);
                    }
                } else {//失败
                    log.error("error_code = " + lotteryBean.getErrorCode() + ";reason = " + lotteryBean.getReason());
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
                LotteryAnalysis.analysisLotteryRecordList(list);

                list = lotteryDao.searchRecent(LotteryTypeConstants.ssq, 60);
                LotteryAnalysis.analysisLotteryRecordList(list);

                List excelList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (i < 50) {
                        excelList.add(LotteryAnalysis.covertLotteryDaoToExcelData(list.get(i), null, null));
                    } else {
                        excelList.add(LotteryAnalysis.covertLotteryDaoToExcelData(list.get(i), null, null));
                    }
                }

                ExcelWriter excelWriter = null;
                try {
                    excelWriter = EasyExcel.write("/home/yinyuan/test_w.xlsx", SsqStatData.class).build();
                    WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
                    excelWriter.write(excelList, writeSheet);
                } finally {
                    // 千万别忘记finish 会帮忙关闭流
                    if (excelWriter != null) {
                        excelWriter.finish();
                    }
                }
            }
        });
    }
}
