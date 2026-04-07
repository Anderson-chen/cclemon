package org.cclemon.api;

import org.cclemon.api.vo.WaterEntryResult;
import org.cclemon.api.vo.command.LogWaterCommand;
import org.cclemon.api.vo.query.WaterQuery;

/**
 * 喝水管理 Handler 介面
 */
public interface WaterHandler {

    WaterEntryResult getToday(WaterQuery query);

    WaterEntryResult logWater(LogWaterCommand command);
}
