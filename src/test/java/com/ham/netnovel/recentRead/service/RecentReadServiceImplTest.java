package com.ham.netnovel.recentRead.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecentReadServiceImplTest {

    private final RecentReadService recentReadService;

    @Autowired
    RecentReadServiceImplTest(RecentReadService recentReadService) {
        this.recentReadService = recentReadService;

    }


    @Test
    void createRecentRead() {
    }

    //테스트 성공
    @Test
    void updateRecentRead() {
        String providerId = "test";

        //존재하지 않는 member 테스트, NoSuchElementException 던져짐

//        String providerId = "ㅇㄹㅇㄴㄻㄴㅇㄹㄴㄹㅇ";

        Long episodeId = 2309L;

        //존재하지 않는 episode 테스트, NoSuchElementException 던져짐
//        Long episodeId = 23232309L;
        recentReadService.updateRecentRead(providerId, episodeId);
    }
}