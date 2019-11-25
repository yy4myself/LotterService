package com.yinyuan.lotter.dao.impl;

import com.yinyuan.lotter.dao.LotteryDao;
import com.yinyuan.lotter.model.LotteryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * 数据库接口的实现类
 */
@Slf4j
@Component
public class LotteryDaoImpl implements LotteryDao {

    private static final String TAG = "LotteryDaoImpl";

    @Autowired
    private MongoTemplate mongotemplate;

    @Override
    public void insert(LotteryRecord lotteryRecord) {
        LotteryRecord record = search(lotteryRecord.getDate(), lotteryRecord.getType());
        if (record == null) {
            log.info(TAG + "record 记录为空,执行插入操作");
            mongotemplate.insert(lotteryRecord);
        } else {
            log.info(TAG + "record 记录不为空,不执行插入操作");
        }
    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

    }

    @Override
    public LotteryRecord search(String date, String type) {
        Query query = new Query(Criteria.where("date").is(date).and("type").is(type));
        LotteryRecord result = mongotemplate.findOne(query, LotteryRecord.class);
        log.info(TAG + "执行查询操作：开奖日期 = " + date + ";类型 = " + type + ";结果 = " + result);
        return result;
    }
}
