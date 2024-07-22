package com.ham.netnovel.member.Service;


import com.ham.netnovel.exception.ServiceMethodException;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.dto.ChangeNickNameDto;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.dto.MemberLoginDto;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
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
