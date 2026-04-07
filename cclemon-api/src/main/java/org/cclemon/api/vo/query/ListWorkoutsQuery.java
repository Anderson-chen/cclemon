package org.cclemon.api.vo.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ListWorkoutsQuery {
    private Long userId;
    private String date;
}
