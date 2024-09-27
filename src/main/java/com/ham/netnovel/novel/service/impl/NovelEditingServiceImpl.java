package com.ham.netnovel.novel.service.impl;

import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.data.NovelStatus;
import com.ham.netnovel.novel.data.NovelType;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.dto.NovelUpdateDto;
import com.ham.netnovel.novel.repository.NovelRepository;
import com.ham.netnovel.novel.service.NovelEditingService;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagDeleteDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
//소설 생성/업데이트 관련 로직을 담는 서비스계층

@Service
@Slf4j
public class NovelEditingServiceImpl implements NovelEditingService {


    private final NovelTagService novelTagService;
    private final MemberService memberService;

    private final NovelRepository novelRepository;

    public NovelEditingServiceImpl(NovelTagService novelTagService, MemberService memberService, NovelRepository novelRepository) {
        this.novelTagService = novelTagService;
        this.memberService = memberService;
        this.novelRepository = novelRepository;
    }


    /**
     * 주어진 소설 생성 DTO를 기반으로 새로운 소설을 생성하는 메서드 입니다.
     *
     * <p>유저 정보를 DB에서 찾고, 없으면 예외로 던집니다.</p>
     * <p>DTO에 등록된 정보로 소설 엔티티를 생성하여 DB에 저장합니다.</p>
     * <p>유저의 ROLE 이 READER 인경우 AUTHOR로 변경하여 DB에 저장합니다.</p>
     *
     * @param novelCreateDto 새 소설의 데이터가 포함된 {@link NovelCreateDto} 객체
     * @return 생성된 소설의 ID
     * @throws NoSuchElementException 해당 작가가 존재하지 않을 경우
     * @throws ServiceMethodException 소설 생성 중 오류가 발생할 경우
     */
    @Override
    @Transactional
    public Long createNovel(NovelCreateDto novelCreateDto) {
        //Member Entity 조회 -> Author 검증
        Member author = memberService.getMember(novelCreateDto.getAccessorProviderId())
                .orElseThrow(() -> new NoSuchElementException("createNovel 메서드 에러, 존재하지 않는 Member 입니다. providerId ="
                        + novelCreateDto.getAccessorProviderId()));

        try {
            //Novel 생성
            Novel targetNovel = Novel.builder()
                    .title(novelCreateDto.getTitle())
                    .description(novelCreateDto.getDescription())
                    .author(author)
                    .type(NovelType.ONGOING)
                    .status(NovelStatus.ACTIVE)
                    .build();

            //DB에 내용 저장
            Novel novel = novelRepository.save(targetNovel);

            //작가가 작성한 태그를 소설에 추가(NovelTag junction table로 매핑)
            //태그명으로 된 태그엔티티가 없는경우, 새로 생성하여 저장됨
            addTagsToNovel(novelCreateDto.getTagNames(), novel.getId());

            //작가가 READER ROLE을 갖고있으면 작가 상태로 변경
            if (author.getRole().equals(MemberRole.READER)) {
                memberService.changeMemberToAuthor(author);
            }
            log.info("새로운 소설 생성 완료! novelId ={}", novel.getId());
            //생성된 Novel의 ID 값 반환
            return novel.getId();
        } catch (Exception ex) {
            //나머지 예외처리
            throw new ServiceMethodException("createNovel 메서드 에러 발생" + ex + ex.getMessage());
        }
    }


    /**
     * 소설 정보를 업데이트하는 메서드입니다.
     * <p>변경되는 소설 정보는 제목, 설명, 타입이며, 변경 사항이 있는 경우에만 업데이트합니다.</p>
     *
     * <p>태그를 변경할 때는 기존의 태그 중 삭제되는 태그는 NovelTag 테이블에서 제거하고,
     * 새로 추가된 태그는 NovelTag 테이블에 추가합니다.</p>
     *
     * @param novelUpdateDto 소설 업데이트에 필요한 정보를 담고 있는 {@link NovelUpdateDto} 객체
     * @return 업데이트 성공 시 true를 반환합니다.
     * @throws AccessDeniedException 요청자가 소설의 작가가 아닌 경우 발생합니다.
     * @throws NoSuchElementException 주어진 novelId에 해당하는 소설이 존재하지 않을 경우 발생합니다.
     * @throws ServiceMethodException 서비스 메서드 실행 중 예외가 발생할 경우 발생합니다.
     */
    @Override
    @Transactional
    public Boolean updateNovel(NovelUpdateDto novelUpdateDto) throws AccessDeniedException {

        //Novel DB 데이터 검증
        Novel novel = novelRepository.findById(novelUpdateDto.getNovelId())
                .orElseThrow(() -> new NoSuchElementException("updateNovel 에러, 존재하지 않는 Novel 입니다. " +
                        "novelId= " + novelUpdateDto.getNovelId()));

        //소설 변경 요청자와 작가가 같은지 검증, 다를경우 예외로 던짐
        validateNovelAuthor(novelUpdateDto.getAccessorProviderId()
                , novel.getAuthor().getProviderId());

        try {
            //유저가 변경하고자 하는 부분만 업데이트(제목/설명/타입)
            updateNovelIfPresent(novel::updateTitle, novelUpdateDto.getTitle());
            updateNovelIfPresent(novel::updateDesc, novelUpdateDto.getDescription());
            updateNovelIfPresent(novel::updateType, novelUpdateDto.getType());
            //DB에 Novel 엔티티 변경내용 저장
            novelRepository.save(novel);

            // 유저가 선택한 태그 이름을 양쪽 공백 제거 후 중복 제거 후 List 객체 생성
            List<String> newTagNames = novelUpdateDto.getTagNames()
                    .stream()
                    .map(String::trim)
                    .distinct()//중복제거
                    .toList();

            for (String newTagName : newTagNames) {
                log.info("태그정보들 ={}",newTagName);
                log.info("------------------------------------");

            }

            //소설과 연결된 Tag들의 이름을 List 객체로 가져옴
            List<String> novelTagList = getNovelTagList(novel);
            // 새로운 태그 리스트와 현재 태그 리스트를 비교하여 업데이트 진행
            updateTagsToNovel(newTagNames, novelTagList,novel.getId());

            //변경이 성공적임을 true를 반환하여 알림
            return true;
        } catch (Exception ex) {
            throw new ServiceMethodException("updateNovel 메서드 에러 발생", ex.getCause());
        }
    }

    /**
     * 작업 요청자와 소설 작가가 동일한지 검증하는 메서드 입니다.
     *
     * @param providerId       작업 요청자의 providerId
     * @param authorProviderId 소설 작가의 providerId
     * @throws AccessDeniedException 작업 요청자와 소설 작가가 다른경우
     */
    private void validateNovelAuthor(String providerId, String authorProviderId)
            throws AccessDeniedException {
        //요청자의 providerId가 작가의 providerId와 같은지 검증
        boolean result = providerId.equals(authorProviderId);
        //다를경우 예외로 던짐
        if (!result) {
            throw new AccessDeniedException("해당 Novel에 접근 권한이 없습니다.");
        }
    }

    /**
     * 주어진 값이 존재하고 유효한 경우, 주어진 업데이트 함수를 사용하여 값을 업데이트하는 메서드 입니다.
     *
     * <p>값이 null이 아니고, 값이 String인 경우에는 비어 있지 않은지 확인합니다.
     * 비어 있지 않은 경우에만 업데이트 함수가 실행됩니다.</p>
     *
     * @param updateFunction 값이 존재할 때 실행할 업데이트 함수
     * @param value          업데이트할 값. null일 수 있으며, String일 경우 비어있지 않아야 합니다.
     */
    private <T> void updateNovelIfPresent(Consumer<T> updateFunction, T value) {
        if (value != null && (!(value instanceof String) || !((String) value).isBlank())) {
            updateFunction.accept(value);
        }
    }

    /**
     * 주어진 태그를 소설에 추가하는 메서드입니다.
     * <p>
     * 태그 이름 리스트가 비어있거나 소설 ID가 null인 경우, 메서드는 아무 작업도 수행하지 않고 종료합니다.
     * </p>
     * <p>
     * 각 태그 이름에 대해 NovelTagCreateDto를 생성하고, 해당 소설 ID와 태그 이름을 설정한 후
     * novelTagService를 통해 소설과 태그 간의 관계를 추가합니다.
     * </p>
     *
     * @param tagNames 추가할 태그 이름의 리스트
     * @param novelId 태그가 추가될 소설의 ID
     */
    private void addTagsToNovel(List<String> tagNames, Long novelId) {

        //태그 이름을 담는 리스트가 비어있거나 novelID가 null이면 메서드 종료
        if (tagNames == null || tagNames.isEmpty() || novelId == null) {
            return;
        }
        //novelTag 엔티티에 소설정보와 태그정보 추가
        //태그명으로 된 엔티티가 없을경우, 새로만들어서 추가
        for (String tagName : tagNames) {
            NovelTagCreateDto build = NovelTagCreateDto.builder()
                    .novelId(novelId)
                    .tagName(tagName)
                    .build();

            //junction table에 소설과 tag 관계 추가
            novelTagService.createNovelTag(build);
        }
    }

    /**
     * 주어진 태그를 소설에서 삭제하는 메서드입니다.
     * <p>
     * 태그 이름 리스트가 비어있거나 소설 ID가 null인 경우, 메서드는 아무 작업도 수행하지 않고 종료합니다.
     * </p>
     * <p>
     * 각 삭제할 태그 이름에 대해 NovelTagDeleteDto를 생성하고, 해당 소설 ID와 태그 이름으로
     * novelTagService를 통해 소설에서 태그를 삭제합니다.
     * </p>
     *
     * @param deleteTagNames 삭제할 태그 이름의 리스트
     * @param novelId 태그가 삭제될 소설의 ID
     */
    private void deletedTagsToNovel(List<String> deleteTagNames,Long novelId) {
        //태그 이름을 담는 리스트가 비어있거나 novelID가 null이면 메서드 종료
        if (deleteTagNames==null || deleteTagNames.isEmpty() || novelId == null) {
            return;
        }
        //반복문을 돌며, NovelTag 엔티티 삭제
        for (String deletedTag : deleteTagNames) {
            NovelTagDeleteDto deleteDto = NovelTagDeleteDto.builder()
                    .tagName(deletedTag)
                    .novelId(novelId)
                    .build();

            //엔티티 삭제
            novelTagService.deleteNovelTag(deleteDto);
        }
    }

    /**
     * 소설의 태그를 업데이트하는 메서드입니다.
     * <p>
     * 기존 태그 목록에서 새로운 태그 목록에 없는 태그는 삭제하고,
     * 새로운 태그 목록에서 기존 태그 목록에 없는 태그는 추가합니다.
     * </p>
     *
     * @param updateTagNames 소설에 추가할 새로운 태그 이름의 리스트
     * @param existingTagNames 소설에 현재 연결된 태그 이름의 리스트
     * @param novelId 태그가 업데이트될 소설의 ID
     */
    private void updateTagsToNovel(List<String> updateTagNames,
                                   List<String> existingTagNames,
                                   Long novelId) {


        // 기존 태그 목록에서 새로운 태그 목록에 없는 태그를 추출하여 삭제

        List<String> deleteTags = existingTagNames.stream()
                .filter(novelTag -> !updateTagNames.contains(novelTag))
                .toList();
        deletedTagsToNovel(deleteTags,novelId);

        // 새로운 태그 목록에서 기존 태그 목록에 없는 태그를 추출하여 추가
        List<String> newTags = updateTagNames
                .stream()
                .filter(novelTag -> !existingTagNames.contains(novelTag))
                .toList();
        addTagsToNovel(newTags, novelId);
    }


    /**
     * 주어진 소설의 태그 목록을 반환하는 메서드입니다.
     * <p>
     * 소설이 null이거나 소설에 연결된 태그 목록이 null인 경우, 빈 리스트를 반환합니다.
     * </p>
     *
     * @param novel 태그 목록을 가져올 {@link Novel} 객체
     * @return 소설에 연결된 태그 이름의 리스트. 소설이 null이거나 태그 목록이 없을 경우 빈 리스트를 반환합니다.
     */
    private List<String> getNovelTagList(Novel novel){
        // 널 체크 및 빈 리스트 반환
        if (novel == null || novel.getNovelTags() == null) {
            return Collections.emptyList();
        }
       return novel.getNovelTags().stream()
                .map(novelTag -> novelTag.getTag().getName())
               .collect(Collectors.toList());
    }




}
