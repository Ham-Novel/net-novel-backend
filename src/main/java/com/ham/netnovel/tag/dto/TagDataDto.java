package com.ham.netnovel.tag.dto;

import com.ham.netnovel.tag.TagStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDataDto {
    private Long id;

    private String name;

    private TagStatus status;
}
