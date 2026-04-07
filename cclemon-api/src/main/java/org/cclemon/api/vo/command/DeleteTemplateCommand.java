package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteTemplateCommand {
    private Long userId;
    private Long templateId;
}
