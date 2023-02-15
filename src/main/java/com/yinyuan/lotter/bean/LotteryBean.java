package com.yinyuan.lotter.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用来接收网络请求返回的结果
 */
@NoArgsConstructor
@Data
public class LotteryBean {

    @JsonProperty("reason")
    private String reason;
    @JsonProperty("error_code")
    private int errorCode;
    @JsonProperty("result")
    private ResultBean result;

    @NoArgsConstructor
    @Data
    public static class ResultBean {
        @JsonProperty("lotteryResList")
        private List<LotteryTermBean> lotteryResList;
        @JsonProperty("page")
        private Integer page;
        @JsonProperty("pageSize")
        private Integer pageSize;
        @JsonProperty("totalPage")
        private Integer totalPage;
    }

    @NoArgsConstructor
    @Data
    public static class LotteryTermBean {

        @JsonProperty("lottery_id")
        private String lotteryId;
        @JsonProperty("lottery_res")
        private String lotteryRes;
        @JsonProperty("lottery_no")
        private String lotteryNo;
        @JsonProperty("lottery_date")
        private String lotteryDate;
        @JsonProperty("lottery_exdate")
        private String lotteryExdate;
        @JsonProperty("lottery_sale_amount")
        private String lotterySaleAmount;
        @JsonProperty("lottery_pool_amount")
        private String lotteryPoolAmount;
    }

}
