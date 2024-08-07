package com.ham.netnovel;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.service.CoinCostPolicyService;
import com.ham.netnovel.comment.CommentRepository;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.commentLike.CommentLike;
import com.ham.netnovel.commentLike.CommentLikeRepository;
import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.commentLike.service.CommentLikeService;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
public class MakeTestRecord {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Autowired
    NovelRepository novelRepository;
    @Autowired
    NovelService novelService;

    @Autowired
    EpisodeRepository episodeRepository;
    @Autowired
    EpisodeService episodeService;

    @Autowired
    CoinCostPolicyRepository costPolicyRepository;
    @Autowired
    CoinCostPolicyService costPolicyService;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;

    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    CommentLikeService commentLikeService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void makeTestItems() {
        //DB records 전부 삭제
        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        episodeRepository.deleteAll();
        costPolicyRepository.deleteAll();
        novelRepository.deleteAll();
        memberRepository.deleteAll();

        //auto_increment id를 1부터 초기화.
        jdbcTemplate.execute("ALTER TABLE novel ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE coin_cost_policy ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE episode ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");

        costPolicyService.createPolicy(CostPolicyCreateDto.builder()
                .name("유료")
                .coinCost(1)
                .build());

        String email = "test";
        String nickName = "testName";
        String providerId = "test";

        IntStream.rangeClosed(1, 100).forEach(i -> {
            MemberCreateDto build = MemberCreateDto.builder()
                    .email(email + i + "@naver.com")
                    .role(MemberRole.READER)
                    .gender(Gender.MALE)
                    .nickName(nickName + i)
                    .providerId(providerId + i)
                    .provider(OAuthProvider.NAVER)
                    .build();
            memberService.createNewMember(build);


            NovelCreateDto novelDto = NovelCreateDto.builder()
                    .title("소설 제목" + i)
                    .description("Duis ea aliquip dolor sit dolore ut adipisicing eu tempor.")
                    .accessorProviderId("test" + i)
                    .build();
            novelService.createNovel(novelDto);

            EpisodeCreateDto episodeDto = EpisodeCreateDto.builder()
                    .novelId(1L)
                    .title("에피소드 제목" + i)
                    .content("Cillum consequat eiusmod consequat anim est.")
                    .costPolicyId(1L)
                    .build();
            episodeService.createEpisode(episodeDto);

            CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                    .episodeId(Long.valueOf(i))
                    .content("댓글 내용" + i)
                    .providerId("test" + i)//테스트용 유저
                    .build();
            commentService.createComment(commentCreateDto);

            int j;
            if (i <= 60) {
                j = 1;
            }
            else if (i <= 90) {
                j = 2;
            }
            else {
                j = 3;
            }
            CommentLikeToggleDto commentLikeToggleDto = CommentLikeToggleDto.builder()
                    .likeType(LikeType.LIKE)
                    .providerId("test" + i)
                    .commentId((long) j)
                    .build();
            commentLikeService.toggleCommentLikeStatus(commentLikeToggleDto);
        });
    }
}
