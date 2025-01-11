package com.ham.netnovel.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "유저 마이페이지 정보 DTO")
public class MemberMyPageDto {

    @Schema(description = "유저의 닉네임", example = "JohnDoe")
    private String nickName;

    @Schema(description = "유저가 보유한 코인 수", example = "1000")
    private Integer coinCount;

    @Schema(description = "유저의 이메일 주소", example = "john@naver.com")
    private String email;



}
