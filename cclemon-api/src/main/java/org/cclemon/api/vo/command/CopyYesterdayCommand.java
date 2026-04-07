package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CopyYesterdayCommand {
    private Long userId;
    private String today;
    private String yesterday;
}
