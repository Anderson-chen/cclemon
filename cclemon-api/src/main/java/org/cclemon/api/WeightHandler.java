package org.cclemon.api;

import org.cclemon.api.vo.PagedResult;
import org.cclemon.api.vo.WeightChartData;
import org.cclemon.api.vo.WeightRecordResult;
import org.cclemon.api.vo.command.DeleteWeightCommand;
import org.cclemon.api.vo.command.RecordWeightCommand;
import org.cclemon.api.vo.query.GetWeightChartQuery;
import org.cclemon.api.vo.query.ListWeightsQuery;

/**
 * 體重管理 Handler 介面
 * <p>
 * 職責: 定義所有與體重聚合根相關的 Command 與 Query 操作。
 * 實作此介面的類別應位於業務模組的 api 套件下，並加上 @Component。
 */
public interface WeightHandler {

    /**
     * 記錄一筆新的體重資料。
     *
     * @param command 記錄體重的命令物件
     * @return 記錄結果
     */
    WeightRecordResult record(RecordWeightCommand command);

    /**
     * 刪除一筆體重資料。
     *
     * @param command 刪除體重的命令物件
     */
    void delete(DeleteWeightCommand command);

    /**
     * 根據條件查詢體重紀錄列表。
     *
     * @param query 查詢條件
     * @return 分頁後的體重紀錄 (框架無關的 DTO)
     */
    PagedResult<WeightRecordResult> list(ListWeightsQuery query);

    /**
     * 取得體重趨勢圖資料。
     *
     * @param query 查詢條件
     * @return 圖表資料
     */
    WeightChartData getChart(GetWeightChartQuery query);
}
