package com.yinyuan.lotter.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SsqStatData {
    @ExcelProperty("期数")
    private int lotteryNo;
    @ExcelProperty("红球")
    private String red;
    @ExcelProperty("蓝球")
    private String blue;
    @ExcelProperty("红球总和")
    private int redTotal;
    @ExcelProperty("间隔")
    private String interval;
    @ExcelProperty("间隔总和")
    private int intervalTotal;
    @ExcelProperty("热号")
    private String hotNum;
    @ExcelProperty("冷号")
    private String coldNum;
    @ExcelProperty("区间")
    private String section;
}
