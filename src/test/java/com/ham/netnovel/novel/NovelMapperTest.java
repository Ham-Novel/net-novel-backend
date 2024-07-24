package com.ham.netnovel.novel;

import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.dto.NovelResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class NovelMapperTest {

    @Test
    public void mapperTest() {
        //given
        Member member = Member.builder()
                .email("smg@hello.com")
                .provider(OAuthProvider.NAVER)
                .providerId("1111")
                .nickName("Jerade")
                .build();

        Novel novel = Novel.builder()
                .title("novel1")
                .description("this is novel desc.")
                .author(member)
                .status(NovelStatus.ONGOING)
                .build();

        log.info("entity = {}", novel.toString());

        //when
        NovelResponseDto responseDto = NovelMapper.INSTANCE.parseResponseDto(novel);

        //then
        log.info("dto = {}", responseDto.toString());

    }

}