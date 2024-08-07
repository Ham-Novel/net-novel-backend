package com.ham.netnovel.tag.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDeleteDto {
    @NotNull
    Long tagId;
}
