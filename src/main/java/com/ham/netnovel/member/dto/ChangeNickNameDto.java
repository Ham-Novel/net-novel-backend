package com.ham.netnovel.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeNickNameDto {

    private String providerId;

    @NotBlank(message = "새 닉네임은 비워둘 수 없습니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자로 작성해 주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "닉네임은 한글 또는 영문으로 작성해주세요")
    private String newNickName;


}
