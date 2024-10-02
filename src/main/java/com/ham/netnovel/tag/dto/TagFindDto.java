package com.ham.netnovel.tag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TagFindDto {

    Long id;

    String tagName;


}
