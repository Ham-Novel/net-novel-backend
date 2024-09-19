package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NovelCRUDService {

    /**
     * 유저가 생성한 Novel을 DB 저장.
     * @param novelCreateDto 생성 정보가 담긴 dto
     * @return 생성한 episode id 값
     */
    Long createNovel(NovelCreateDto novelCreateDto);

    /**
     * DB에 저장된 Novel 데이터 변경.
     * @param novelUpdateDto 업데이트 정보가 담긴 dto
     */
    void updateNovel(NovelUpdateDto novelUpdateDto);

    /**
     * DB에 저장된 Novel 삭제.
     * @param novelDeleteDto 삭제 정보가 담긴 dto
     */
    void deleteNovel(NovelDeleteDto novelDeleteDto);

}
