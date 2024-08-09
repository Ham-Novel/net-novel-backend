package com.ham.netnovel.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateDto {
    @NotBlank
    String name;
}

