package com.ham.netnovel;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.service.CoinCostPolicyService;
import com.ham.netnovel.comment.CommentRepository;
import com.ham.netnovel.comment.dto.CommentCreateDto;
import com.ham.netnovel.comment.service.CommentService;
import com.ham.netnovel.commentLike.CommentLikeRepository;
import com.ham.netnovel.commentLike.LikeType;
import com.ham.netnovel.commentLike.dto.CommentLikeToggleDto;
import com.ham.netnovel.commentLike.service.CommentLikeService;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelRating.NovelRatingRepository;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import com.ham.netnovel.novelRating.service.NovelRatingService;
import com.ham.netnovel.novelTag.NovelTagRepository;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.dto.NovelTagListDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import com.ham.netnovel.tag.TagRepository;
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Random;
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
    NovelRatingRepository novelRatingRepository;
    @Autowired
    NovelRatingService novelRatingService;

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
    TagRepository tagRepository;
    @Autowired
    TagService tagService;

    @Autowired
    NovelTagRepository novelTagRepository;
    @Autowired
    NovelTagService novelTagService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void makeTestItems() {
        //DB records 전부 삭제
        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        episodeRepository.deleteAll();
        costPolicyRepository.deleteAll();
        novelRatingRepository.deleteAll();
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
        Random random = new Random();

        //좋아요 랜덤 설정
        long likesFirst = random.nextLong(50);
        long likesSecond = random.nextLong(90);

        IntStream.rangeClosed(1, 100).forEach(i -> {

            //유저 생성
            MemberCreateDto build = MemberCreateDto.builder()
                    .email(email + i + "@naver.com")
                    .role(MemberRole.READER)
                    .gender(Gender.MALE)
                    .nickName(nickName + i)
                    .providerId(providerId + i)
                    .provider(OAuthProvider.NAVER)
                    .build();
            memberService.createNewMember(build);

            //작품 생성
            NovelCreateDto novelDto = NovelCreateDto.builder()
                    .title("소설 제목" + i)
                    .description("Duis ea aliquip dolor sit dolore ut adipisicing eu tempor.")
                    .accessorProviderId("test" + i)
                    .build();
            novelService.createNovel(novelDto);

            //작품 별점 생성
            int randomRating = random.nextInt(1,10);
            NovelRatingSaveDto ratingSaveDto = NovelRatingSaveDto.builder()
                    .novelId(1L)
                    .providerId("test" + i)
                    .rating(randomRating)
                    .build();
            novelRatingService.saveNovelRating(ratingSaveDto);

            //에피소드 생성
            EpisodeCreateDto episodeDto = EpisodeCreateDto.builder()
                    .novelId(1L)
                    .title("에피소드 제목" + i)
                    .content("Cillum consequat eiusmod consequat anim est.")
                    .costPolicyId(1L)
                    .build();
            episodeService.createEpisode(episodeDto);

            //댓글 생성
            CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                    .episodeId(Long.valueOf(i))
                    .content("댓글 내용" + i)
                    .providerId("test" + i)//테스트용 유저
                    .build();
            commentService.createComment(commentCreateDto);

            //댓글 좋아요 생성
            long commentId = 1L;
            if (i >= 70) {
                commentId = likesFirst;
            }
            else if (i >= 90) {
                commentId = likesSecond;
            }
            CommentLikeToggleDto commentLikeToggleDto = CommentLikeToggleDto.builder()
                    .likeType(LikeType.LIKE)
                    .providerId("test" + i)
                    .commentId(commentId)
                    .build();
            commentLikeService.toggleCommentLikeStatus(commentLikeToggleDto);
        });
    }

    @Test
    void makeTagItems() {
        novelTagRepository.deleteAll();
        tagRepository.deleteAll();

        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE novel_tag ALTER COLUMN id RESTART WITH 1");


        Random random = new Random();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            //태그 엔티티 생성
            TagCreateDto tagCreateDto = TagCreateDto.builder().name("태그" + i).build();
            tagService.createTag(tagCreateDto);

            //테그 노벨 엔티티 생성
            long randNovelId = random.nextLong(1, 10);
            NovelTagCreateDto novelTagCreateDto = NovelTagCreateDto.builder()
                    .tagName("태그" + i)
                    .novelId(randNovelId)
                    .build();
            novelTagService.createNovelTag(novelTagCreateDto);
        });
    }
}
