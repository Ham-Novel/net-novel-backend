package com.ham.netnovel.novel.service;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novel.dto.*;
import com.ham.netnovel.novelAverageRating.NovelAverageRating;
import com.ham.netnovel.novelRanking.service.NovelRankingService;
import com.ham.netnovel.s3.S3Service;
import com.ham.netnovel.tag.dto.TagDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NovelServiceImpl implements NovelService {
    private final NovelRepository novelRepository;
    private final MemberService memberService;

    private final NovelRankingService novelRankingService;

    private final S3Service s3Service;

    @Autowired
    public NovelServiceImpl(NovelRepository novelRepository, MemberService memberService, NovelRankingService novelRankingService, S3Service s3Service) {
        this.novelRepository = novelRepository;
        this.memberService = memberService;
        this.novelRankingService = novelRankingService;
        this.s3Service = s3Service;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Novel> getNovel(Long novelId) {
        return novelRepository.findById(novelId);
    }

    @Override
    public List<Novel> getNovelsByAuthor(String providerId) {
        return novelRepository.findNovelsByMember(providerId);
    }

    @Override
    @Transactional
    public void createNovel(NovelCreateDto novelCreateDto) {
        log.info("Novel 생성 = {}", novelCreateDto.toString());

        //Member Entity 조회 -> Author 검증
        Member author = memberService.getMember(novelCreateDto.getAccessorProviderId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Member 입니다."));

        try {
            //Novel 생성
            Novel targetNovel = Novel.builder()
                    .title(novelCreateDto.getTitle())
                    .description(novelCreateDto.getDescription())
                    .author(author)
                    .type(NovelType.ONGOING)
                    .status(NovelStatus.ACTIVE)
                    .build();
            novelRepository.save(targetNovel);
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovel 메서드 에러 발생", ex.getCause());
        }

    }

    @Override
    @Transactional
    public void updateNovel(NovelUpdateDto novelUpdateDto) {
        log.info("Novel 변경 = {}", novelUpdateDto.toString());

        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelUpdateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelUpdateDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 없습니다.");
            }

            //updateDto에서 특정 property 값만 업데이트하는 로직.
            //null 값과 빈 String은 제외시키고 update 작업을 수행.
            if (novelUpdateDto.getTitle() != null && !novelUpdateDto.getTitle().isBlank()) {
                targetNovel.updateTitle(novelUpdateDto.getTitle());
            }
            if (novelUpdateDto.getDescription() != null && !novelUpdateDto.getDescription().isBlank()) {
                targetNovel.updateDesc(novelUpdateDto.getDescription());
            }
            if (novelUpdateDto.getType() != null) {
                targetNovel.updateType(novelUpdateDto.getType());
            }

            novelRepository.save(targetNovel);
        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional
    public void deleteNovel(NovelDeleteDto novelDeleteDto) {
        log.info("Novel 삭제 = {}", novelDeleteDto.toString());

        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelDeleteDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            //Novel 변경 권한 검증
            boolean isAuthor = novelDeleteDto.getAccessorProviderId().equals(targetNovel.getAuthor().getProviderId());
            if (!isAuthor) {
                throw new AccessDeniedException("해당 Novel에 접근 권한이 업습니다.");
            }
        } catch (Exception ex) {
            throw new ServiceMethodException("deleteNovel 메서드 에러 발생", ex.getCause());
        }

        //Novel 삭제 처리
        targetNovel.changeStatus(NovelStatus.DELETED_BY_USER);
        novelRepository.save(targetNovel);
    }

    @Override
    @Transactional(readOnly = true)
    public NovelInfoDto getNovelInfo(Long novelId) {
        //Novel DB 데이터 검증
        Novel targetNovel = novelRepository.findById(novelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Novel 입니다."));

        try {
            return convertEntityToInfoDto(targetNovel);
        } catch (Exception ex) {
            throw new ServiceMethodException("getNovelInfo 메서드 에러 발생: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NovelInfoDto> getNovelsRecent(Pageable pageable) {
        List<Novel> recentNovels = novelRepository.findByLatestEpisodesOrderByCreatedAt(pageable);
        return recentNovels.stream()
                .map(this::convertEntityToInfoDto)
                .toList();
    }

    //단순히 엔티티 List만 반환하는 메서드
    //Null체크, DTO 변환은 MemberMyPageService에서 진행
    @Override
    @Transactional(readOnly = true)
    public List<Novel> getFavoriteNovels(String providerId) {
        try {
            return novelRepository.findFavoriteNovelsByMember(providerId);
        } catch (Exception ex) {//예외 발생시 처리
            throw new ServiceMethodException("getFavoriteNovels 메서드 에러 발생", ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getRatedNovelIds() {
        try {
            return novelRepository.findByNovelRating()
                    .stream()
                    .map(Novel::getId)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            throw new ServiceMethodException("getRatedNovelIds 메서드 에러 발생", ex.getCause());

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
            for (Map<String, Object> rankingDatas : rankingFromRedis) {
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

    @Override
    @Transactional
    public boolean updateNovelThumbnail(MultipartFile file, Long novelId, String providerId) {
        //파라미터 null 체크
        if (file.isEmpty()||novelId==null||providerId==null){
            throw new IllegalArgumentException("saveNovelThumbnail 메서드 에러, 파라미터가 null 입니다.");
        }

        /*
        Novel 엔티티를 DB에서 찾아옴
        만약 Novel 엔티티가 Null 이거나, 작가정보와 섬네일 업로드 요청자 정보가 일치하지 않는경우 예외로 던짐
         */
        Novel novel = getNovel(novelId)
                .filter(foundNovel -> foundNovel.getAuthor().getProviderId().equals(providerId))
                .orElseThrow(() -> new IllegalArgumentException("saveNovelThumbnail 메서드 에러, 섬네일 업로드 요청자와, 소설 작가가 다릅니다."));

        try {
            //AWS S3에 파일 업로드 후 파일명 반환 , S3 업로드 실패시 예외로 던져짐
            String fileName = s3Service.uploadFileToS3(file);
            //Novel 엔티티의 섬네일 필드값 수정
            novel.updateThumbnailFileName(fileName);
            //수정된 Novel 엔티티 DB에 저장
            novelRepository.save(novel);
            //true 반환
            return true;

        } catch (Exception ex) {
            throw new ServiceMethodException("saveNovelThumbnail 메서드 에러, 섬네일 변경에 실패했습니다." + ex + ex.getMessage());
        }

    }

    NovelListDto convertEntityToListDto(Novel novel){
        //작품의 태그들 가져오기
        List<TagDataDto> dataDtoList = novel.getNovelTags().stream()
                .map(novelTag -> novelTag.getTag().getData())
                .toList();

        //AWS cloud front 섬네일 이미지 URL 객체 반환
        String thumbnailUrl = s3Service.generateCloudFrontUrl(novel.getThumbnailFileName());

        //DTO 반환
       return NovelListDto.builder()
                .thumbnailUrl(String.valueOf(thumbnailUrl))
                .title(novel.getTitle())
                .authorName(novel.getAuthor().getNickName())
                .id(novel.getId())
                .favoriteCount(novel.getFavorites().size())
                .tags(dataDtoList).build();

    }

    NovelInfoDto convertEntityToInfoDto(Novel novel) {
        //평균 별점 레코드가 없으면 0점짜리 새로 생성
        NovelAverageRating averageRating = Optional.ofNullable(novel.getNovelAverageRating())
                .orElse(NovelAverageRating.builder()
                        .novel(novel)
                        .averageRating(BigDecimal.valueOf(0))
                        .ratingCount(0)
                        .build());

        //작품의 태그들 가져오기
        List<TagDataDto> dataDtoList = novel.getNovelTags().stream()
                .map(novelTag -> novelTag.getTag().getData())
                .toList();

        //AWS cloud front 섬네일 이미지 URL 객체 반환
        String thumbnailUrl = s3Service.generateCloudFrontUrl(novel.getThumbnailFileName());

        //작품의 모든 에피소드 조회수 총합
        int viewsSum = novel.getEpisodes().stream().mapToInt(Episode::getView).sum();

        return NovelInfoDto.builder()
                .id(novel.getId())
                .title(novel.getTitle())
                .desc(novel.getDescription())
                .authorName(novel.getAuthor().getNickName())
                .views(viewsSum)
                .averageRating(averageRating.getAverageRating())
                .episodeCount(novel.getEpisodes().size())
                .favoriteCount(novel.getFavorites().size())
                .tags(dataDtoList)
                .thumbnailUrl(thumbnailUrl)//섬네일
                .build();
    }



}