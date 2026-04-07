package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WaterEntryResult {
    String id;
    String userId;
    String date;
    Integer totalMl;
    List<WaterLogEntryResult> logs;
}
