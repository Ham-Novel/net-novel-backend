package com.ham.netnovel.member.service.impl;


import com.ham.netnovel.common.exception.NotEnoughCoinsException;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.dto.MemberMyPageDto;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.member.dto.ChangeNickNameDto;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.dto.MemberLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;


    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    //가져다 쓸경우 Null체크 필수!!
    @Override
    public Optional<Member> getMember(String providerId) {
        return memberRepository.findByProviderId(providerId);
    }

    @Override
    public MemberLoginDto getMemberLoginInfo(String providerId) {

//        Optional<Member> optionalMember = memberRepository.findByProviderId(providerId);

//        Member 엔티티 조회, null 체크는 getMember 메서드에서 진행
        Optional<Member> optionalMember = getMember(providerId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return MemberLoginDto.builder()
                    .providerId(member.getProviderId())
                    .nickName(member.getNickName())
                    .role(member.getRole())
                    .gender(member.getGender())
                    .build();
        } else {
            return new MemberLoginDto();
        }


    }

    @Override
    @Transactional
    public void createNewMember(MemberCreateDto memberCreateDto) {

        //파라미터로 받은 DTO를 Member 엔티티로 변환
        try {
            Member member = new Member(memberCreateDto.getEmail(),
                    memberCreateDto.getProvider(),
                    memberCreateDto.getProviderId(),
                    memberCreateDto.getRole(),
                    memberCreateDto.getNickName(),
                    memberCreateDto.getGender(),
                    0);//초기 코인 갯수 0개 설정

            //새로운 유저 저장
            memberRepository.save(member);


        } catch (Exception ex) {//예외 발생시 RuntimeException으로 던짐, 트랜잭션 자동롤백
            throw new RuntimeException("create member error");
        }


    }

    @Override
    @Transactional
    public void updateMemberNickName(ChangeNickNameDto changeNickNameDto) {

        Member member = getMember(changeNickNameDto.getProviderId())
                .orElseThrow(() -> new NoSuchElementException("유저 정보 없음"));


        //닉네임 패턴 검증, 2~20 길이의 한글,영문 닉네임, 특수문자는 허용하지 않음
        //규칙에 어긋날경우 예외로 던짐
        String nickName = validNickName(changeNickNameDto.getNewNickName());
        try {
            //엔티티 닉네임 변경
            member.changeNickName(nickName);
            memberRepository.save(member);
        } catch (Exception ex) {
            //에러 발생시 예외로 던지고 트랜잭션 롤백
            throw new ServiceMethodException("updateMemberNickName 메서드 에러 발생");
        }

        //DB에 변경사항 저장


    }

    @Override
    @Transactional
    public void deductMemberCoins(String providerId, Integer coinAmount) {

        //사용한 코인 수가 올바르지 않을때 예외로 던짐
        if (coinAmount == null || coinAmount < 0) {
            throw new IllegalArgumentException("deductMemberCoins 에러, coinAmount 값이 유효하지 않습니다.");
        }

        Optional<Member> optionalMember = getMember(providerId);
        if (optionalMember.isEmpty()) {
            throw new NoSuchElementException("deductMemberCoins 에러, 유저 정보가 없습니다.");
        }
        //Optional 벗기기
        Member member = optionalMember.get();

        //유저의 전체 코인수 변수에 저장
        Integer totalCoin = member.getCoinCount();

        //유저의 코인이 null일경우 예외처리
        if (totalCoin == null) {
            throw new NoSuchElementException("deductMemberCoins 에러, 유저 정보가 없습니다.");
            //유저의 코인이 사용한 코인수보다 적을때 예외처리
        } else if (totalCoin < coinAmount) {
            throw new NotEnoughCoinsException("deductMemberCoins 에러, 유저의 코인이 부족합니다.");
        }

        try {
            //엔티티의 coinAmount 차감
            member.deductMemberCoins(coinAmount);
            //엔티티 DB에 저장
            memberRepository.save(member);
        } catch (Exception ex) {
            throw new ServiceMethodException("deductMemberCoins 메서드 에러 발생" + ex.getMessage());
        }


    }

    @Override
    @Transactional
    public void increaseMemberCoins(Member member, int coinAmount) {
        try {
            //멤버 엔티티 코인수 증가
            member.increaseMemberCoins(coinAmount);
            //엔티티 저장
            memberRepository.save(member);
        } catch (Exception ex) {
            throw new ServiceMethodException("increaseMemberCoins 메서드 에러 발생" + ex.getMessage());
        }


    }

    @Override
    @Transactional(readOnly = true)
    public MemberMyPageDto getMemberMyPageInfo(String providerId) {

        //유저 정보가 없을경우 예외로 던짐
        Member member = getMember(providerId)
                .orElseThrow(() -> new NoSuchElementException("유저 정보 없음"));
        try {
            //유저 정보 DTO로 변환하여 반환
            return MemberMyPageDto.builder()
                    .email(member.getEmail())
                    .coinCount(member.getCoinCount())
                    .nickName(member.getNickName())
                    .build();
        } catch (Exception ex) {
            throw new ServiceMethodException("getMemberMyPageInfo 메서드 에러 발생" + ex.getMessage());
        }


    }

    @Override
    @Transactional
    public void changeMemberToAuthor(Member member) {
        //파라미터 null체크
        if (member==null){
            throw new NoSuchElementException("changeMemberToAuthor 메서드 에러, 유저정보 없음");
        }
        try {
            //유저 ROLE 이 이미 작가일경우 메서드 종료
            if (member.getRole().equals(MemberRole.AUTHOR)){
                log.info("유저 Role 이미 AUTHOR 입니다. 메서드 종료");
                return;
            }
            member.changeRoleToAuthor();
            //DB 에 내용 업데이트
            memberRepository.save(member);
            log.info("유저 {} ROLE AUTHOR 로 변경 완료.",member.getId());

        }catch (Exception ex){
            throw new ServiceMethodException("changeMemberToAuthor 메서드 에러, 예외내용 = "+ ex.getMessage());
        }
    }


    private String validNickName(String nickName) {
        int minLength = 2;
        int maxLength = 20;//20자이상 닉네임 사용 금지

        //한글, 영어대소문자만 허용
        String regex = "^[가-힣a-zA-Z]+$";

        if (nickName.isEmpty() || nickName.length() < minLength || nickName.length() > maxLength || !nickName.matches(regex)) {
            throw new IllegalArgumentException("닉네임이 올바르지 않습니다.");
        }
        return nickName;

    }
}
