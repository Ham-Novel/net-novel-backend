package com.ham.netnovel.novel.service.impl;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.common.utils.TypeValidationUtil;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.data.NovelSearchType;
import com.ham.netnovel.novel.data.NovelSortOrder;
import com.ham.netnovel.novel.dto.NovelFavoriteDto;
import com.ham.netnovel.novel.dto.NovelListDto;
import com.ham.netnovel.novel.repository.NovelRepository;
import com.ham.netnovel.novel.service.NovelSearchService;
import com.ham.netnovel.novelRanking.service.NovelRankingService;
import com.ham.netnovel.s3.S3Service;
import com.ham.netnovel.tag.dto.TagDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//랭킹, 유저의선호작품, 검색어로 검색 등 소설을 특정한 조건으로 가져오는 로직을 담는 서비스계층
@Service
@Slf4j
public class NovelSearchServiceImpl implements NovelSearchService {


    private final NovelRepository novelRepository;

    private final S3Service s3Service;

    private final NovelRankingService novelRankingService;

    public NovelSearchServiceImpl(NovelRepository novelRepository, S3Service s3Service, NovelRankingService novelRankingService) {
        this.novelRepository = novelRepository;
        this.s3Service = s3Service;
        this.novelRankingService = novelRankingService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NovelListDto> getNovelsBySearchCondition(String sortOrder,
                                                         Pageable pageable,
                                                         List<Long> tagIds) {
        //정렬 디폴드값은 조회수
        NovelSortOrder novelSortOrder = NovelSortOrder.VIEWCOUNT;
        //파라미터에 따라 정렬 방법 변경
        switch (sortOrder) {
            case "favorites" -> novelSortOrder = NovelSortOrder.FAVORITES;
            case "latest" -> novelSortOrder = NovelSortOrder.LATEST;
        }
        try {
            List<NovelListDto> novelListDtos = novelRepository.findNovelsBySearchConditions(novelSortOrder, pageable, tagIds);
            return generateThumbnailUrls(novelListDtos);
        } catch (Exception ex) {
            throw new ServiceMethodException("getNovelsBySearchCondition 메서드 에러" + ex + ex.getMessage());

        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<NovelListDto> getNovelsBySearchWord(String searchWord, NovelSearchType novelSearchType, Pageable pageable) {


        //SQL Injection 공격 방지, 주석문자 긴공백 제거
        String validateWord = TypeValidationUtil.validateSearchWord(searchWord);

        try {
            List<NovelListDto> novelListDtos;
            // 검색 타입에 따른 검색 로직 처리
            switch (novelSearchType) {
                //작가이름 검색
                case AUTHOR_NAME -> novelListDtos = novelRepository.findByAuthorName(validateWord, pageable);
                //소설제목 검색
                default -> novelListDtos = novelRepository.findBySearchWord(validateWord, pageable);
            }
            return generateThumbnailUrls(novelListDtos);

        } catch (Exception ex) {
            throw new ServiceMethodException("getNovelsBySearchWord 메서드 에러" + ex.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<NovelListDto> getNovelsByRanking(String period, Pageable pageable) {
        try {
            // 페이지 번호와 페이지 크기를 사용해 데이터의 시작 인덱스를 계산
            int startIndex = pageable.getPageNumber() * pageable.getPageSize();
            // 데이터의 끝 인덱스를 계산 (시작 인덱스 + 페이지 크기 - 1)
            int endIndex = startIndex + pageable.getPageSize() - 1;

            // Redis에서 주어진 기간(period)에 해당하는 소설 랭킹 데이터를 가져옴
            // 기간은 daily, weekly, monthly 중 하나로 전달
            // startIndex와 endIndex를 사용해 Redis에서 가져올 데이터 범위를 설정
            // 리턴되는 리스트는 각 소설의 ID와 랭킹 정보를 포함하는 맵(Map)의 리스트임
            List<Map<String, Object>> rankingFromRedis = novelRankingService.getNovelRankingFromRedis(period, startIndex, endIndex);


            // 현재 페이지에 해당하는 소설 ID를 추출
            List<Long> novelIds = new ArrayList<>();
            for (Map<java.lang.String, Object> rankingDatas : rankingFromRedis) {
                // 랭킹 데이터에서 "novelId" 값을 추출하여 novelIds 리스트에 추가
                novelIds.add((Long) rankingDatas.get("novelId"));
            }

            // 소설 엔티티를 조회한 후, 랭킹 순서대로 정렬하고 DTO로 변환하여 반환
            // JPA는 기본적으로 ID 순으로 정렬하므로, 랭킹 순서대로 정렬
            return novelRepository.findByNovelIds(novelIds)
                    .stream()
                    .sorted(Comparator.comparing(novel -> novelIds.indexOf(novel.getId()))) // 랭킹 순서로 정렬(JPA는 엔티티 id 순서로 정렬함)
                    .map(this::convertEntityToListDto)//엔티티 DTO로 변환
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            throw new ServiceMethodException("getNovelsByRanking 메서드 에러 발생" + ex.getMessage());

        }

    }
    //단순히 엔티티 List만 반환하는 메서드
    //Null체크, DTO 변환은 MemberMyPageService에서 진행
    @Override
    @Transactional(readOnly = true)
    public List<NovelFavoriteDto> getFavoriteNovels(String providerId) {
        try {
            //유저 providerId 값으로 선호하는 작품을DTO로 변환하여 List객체에 담아 반환
            return novelRepository.findFavoriteNovelsByMember(providerId)
                    .stream()
                    .map(this::convertEntityToFavoriteDto)//DTO로 변환
                    .collect(Collectors.toList());//List 객체에 담음
        } catch (Exception ex) {//예외 발생시 처리
            throw new ServiceMethodException("getFavoriteNovels 메서드 에러 발생", ex.getCause());
        }
    }


    /**
     * 랭킹과 같이 대량의 소설정보를 전달시 사용하는 DTO로 변환하는 메서드 입니다.
     *
     * @param novel 소설 엔티티
     * @return NovelListDto
     */
    NovelListDto convertEntityToListDto(Novel novel) {
        //작품의 태그들 가져오기
        List<TagDataDto> dataDtoList = novel.getNovelTags().stream()
                .map(novelTag -> novelTag.getTag().getData())
                .toList();

        //AWS cloud front 섬네일 이미지 URL 객체 반환
        String thumbnailUrl = s3Service.generateCloudFrontUrl(novel.getThumbnailFileName(), "mini");

        //DTO 반환
        return NovelListDto.builder()
                .thumbnailUrl(String.valueOf(thumbnailUrl))
                .title(novel.getTitle())
                .authorName(novel.getAuthor().getNickName())
                .id(novel.getId())
                .totalFavorites(novel.getFavorites().size())
                .tags(dataDtoList).build();
    }

    NovelFavoriteDto convertEntityToFavoriteDto(Novel novel){

        //작품의 태그들 가져오기
        List<TagDataDto> dataDtoList = novel.getNovelTags().stream()
                .map(novelTag -> novelTag.getTag().getData())
                .toList();

        //AWS cloud front 섬네일 이미지 URL 객체 반환
        String thumbnailUrl = s3Service.generateCloudFrontUrl(novel.getThumbnailFileName(), "mini");

        //DTO 반환
        return NovelFavoriteDto.builder()
                .thumbnailUrl(String.valueOf(thumbnailUrl))
                .title(novel.getTitle())
                .authorName(novel.getAuthor().getNickName())
                .id(novel.getId())
                .build();

    }


    //섬네일 파일 이미지 이름으로, CloudFront URL을 생성하는 메서드
    private List<NovelListDto> generateThumbnailUrls(List<NovelListDto> novelListDtos) {

        return novelListDtos.stream()
                .peek(novelListDto -> {
                    String cloudFrontUrl = s3Service.
                            generateCloudFrontUrl(novelListDto.getThumbnailUrl(), "mini");//소설 섬네일 URL 생성
                    novelListDto.setThumbnailUrl(cloudFrontUrl);//DTO에 섬네일 URL 할당
                }).collect(Collectors.toList());
    }
}
