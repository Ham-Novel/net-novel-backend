package com.ham.netnovel.member.Service;


import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.dto.MemberLoginDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    //가져다 쓸경우 Null체크 필수!!
    @Override
    public Optional<Member> getMember(String providerId) {
        Optional<Member> memberOptional = memberRepository.findByProviderId(providerId);
        return memberOptional;
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


        }catch (Exception ex){//예외 발생시 RuntimeException으로 던짐, 트랜잭션 자동롤백
            throw new RuntimeException("create member error");
        }


    }
}
