package com.ham.netnovel;

import com.ham.netnovel.coinChargeHistory.dto.CoinChargeCreateDto;
import com.ham.netnovel.coinChargeHistory.service.CoinChargeHistoryService;
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
import com.ham.netnovel.episode.Episode;
import com.ham.netnovel.episode.EpisodeRepository;
import com.ham.netnovel.episode.dto.EpisodeCreateDto;
import com.ham.netnovel.episode.dto.EpisodeDetailDto;
import com.ham.netnovel.episode.service.EpisodeManagementServiceImpl;
import com.ham.netnovel.episode.service.EpisodeService;
import com.ham.netnovel.episodeViewCount.EpisodeViewCountRepository;
import com.ham.netnovel.favoriteNovel.FavoriteNovelRepository;
import com.ham.netnovel.favoriteNovel.service.FavoriteNovelService;
import com.ham.netnovel.member.Member;
import com.ham.netnovel.member.MemberRepository;
import com.ham.netnovel.member.OAuthProvider;
import com.ham.netnovel.member.data.Gender;
import com.ham.netnovel.member.data.MemberRole;
import com.ham.netnovel.member.dto.MemberCreateDto;
import com.ham.netnovel.member.service.MemberService;
import com.ham.netnovel.novel.Novel;
import com.ham.netnovel.novel.NovelRepository;
import com.ham.netnovel.novel.dto.NovelCreateDto;
import com.ham.netnovel.novel.service.NovelService;
import com.ham.netnovel.novelAverageRating.NovelAverageRatingRepository;
import com.ham.netnovel.novelRating.NovelRatingRepository;
import com.ham.netnovel.novelRating.dto.NovelRatingSaveDto;
import com.ham.netnovel.novelRating.service.NovelRatingService;
import com.ham.netnovel.novelTag.NovelTagRepository;
import com.ham.netnovel.novelTag.dto.NovelTagCreateDto;
import com.ham.netnovel.novelTag.service.NovelTagService;
import com.ham.netnovel.recentRead.RecentReadId;
import com.ham.netnovel.recentRead.RecentReadRepository;
import com.ham.netnovel.recentRead.service.RecentReadService;
import com.ham.netnovel.tag.TagRepository;
import com.ham.netnovel.tag.dto.TagCreateDto;
import com.ham.netnovel.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
public class DBRecordTest {
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
    EpisodeManagementServiceImpl episodeManagementService;

    @Autowired
    NovelAverageRatingRepository averageRatingRepository;
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
    RecentReadService recentReadService;
    @Autowired
    RecentReadRepository recentReadRepository;

    @Autowired
    FavoriteNovelRepository favoriteNovelRepository;
    @Autowired
    FavoriteNovelService favoriteNovelService;

    @Autowired
    EpisodeViewCountRepository episodeViewCountRepository;

    @Autowired
    CoinChargeHistoryService coinChargeHistoryService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //DB records 전부 삭제
    @Test
    void clear() {

        recentReadRepository.deleteAll();
        novelTagRepository.deleteAll();
        favoriteNovelRepository.deleteAll();

        commentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        episodeRepository.deleteAll();
        costPolicyRepository.deleteAll();

        averageRatingRepository.deleteAll();
        novelRatingRepository.deleteAll();
        tagRepository.deleteAll();
        novelRepository.deleteAll();

        memberRepository.deleteAll();

        //auto_increment id를 1부터 초기화.
        jdbcTemplate.execute("ALTER TABLE novel ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE coin_cost_policy ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE episode ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE novel_tag ALTER COLUMN id RESTART WITH 1");

    }

    @Test
    void makeBasicItems() {
        costPolicyService.createPolicy(CostPolicyCreateDto.builder()
                .name("무료")
                .coinCost(0)
                .build());

        costPolicyService.createPolicy(CostPolicyCreateDto.builder()
                .name("유료")
                .coinCost(1)
                .build());

        String email = "test";
        String nickName = "testName";
        String providerId = "test";
        Random random = new Random();

        //좋아요 랜덤 설정
        long likesFirst = random.nextLong(70);
        long likesSecond = random.nextLong(50);

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
            int randomRating = random.nextInt(1, 10);
            NovelRatingSaveDto ratingSaveDto = NovelRatingSaveDto.builder()
                    .novelId(1L)
                    .providerId("test" + i)
                    .rating(randomRating)
                    .build();
            novelRatingService.saveNovelRating(ratingSaveDto);

            //에피소드 생성
            long policyId;
            if (i <= 15) {
                policyId = 1L;
            } else {
                policyId = 2L;
            }
            EpisodeCreateDto episodeDto = EpisodeCreateDto.builder()
                    .novelId(1L)
                    .title("에피소드 제목" + i)
                    .content("Cillum consequat eiusmod consequat anim est.")
                    .costPolicyId(policyId)
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
            } else if (i >= 50) {
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
    void makeEpisodeItems() {
//        episodeRepository.deleteAll();
//        jdbcTemplate.execute("ALTER TABLE episode ALTER COLUMN id RESTART WITH 1");

        Random random = new Random();
        IntStream.rangeClosed(1, 50).forEach(i->{
            long next = random.nextLong(1, 10);
            episodeService.createEpisode(EpisodeCreateDto.builder()
                            .novelId(next)
                            .title("테스트 데이터"+i)
                            .costPolicyId(1L)
                            .content("콘텐츠입니다")
                    .build());
        });
    }

    //에피소드 더미 데이터에 테스트 조회수 넣기
    @Test
    @Transactional
    @Commit
    void getEpisodeDetail() {
        Random random = new Random();
        IntStream.rangeClosed(1, 100).forEach(i->{
            long epi_id = random.nextLong(101, 110);
            String user_id = "test" + random.nextLong(1, 10);
            EpisodeDetailDto episodeDetail = episodeManagementService.getEpisodeDetail(user_id, epi_id);
            System.out.println(episodeDetail.toString());

        });
        episodeManagementService.updateEpisodeViewCountFromRedis();
    }

    @Test
    void makeTagItems() {
//        novelTagRepository.deleteAll();
//        tagRepository.deleteAll();
//
//        jdbcTemplate.execute("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");
//        jdbcTemplate.execute("ALTER TABLE novel_tag ALTER COLUMN id RESTART WITH 1");


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

    //댓글생성
    @Test
    void makeCommentItems() {
        Long episodeId = 4L;//댓글 생성할 에피소드
        String providerId = "test1";//작성자

        int max = 10;//생성할 엔티티 번호 max
        IntStream.rangeClosed(1, max).forEach(i -> {
            CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                    .episodeId(episodeId)
                    .content("댓글 내용")
                    .providerId(providerId)//작성자
                    .build();
            commentService.createComment(commentCreateDto);
        });
    }

    @Test
    void makeCommentLikeItems(){

        String providerId = "test1";//좋아요 누른 유저
        Long commentId = 3L;//좋아요 누를 댓글 id

        int max = 10;//생성할 엔티티 번호 max

        IntStream.rangeClosed(1, max).forEach(i -> {
            CommentLikeToggleDto commentLikeToggleDto = CommentLikeToggleDto.builder()
                    .likeType(LikeType.LIKE)
                    .providerId("test" + i)
                    .commentId(commentId)
                    .build();
            commentLikeService.toggleCommentLikeStatus(commentLikeToggleDto);

                });
    }

    @Test
    @Transactional
    @Commit
    void makeHistoryItems() {
        recentReadRepository.deleteAll();
//        jdbcTemplate.execute("ALTER TABLE recent_read ALTER COLUMN id RESTART WITH 1");

//        Random random = new Random();
//        long next = random.nextLong(1, 5);

        Member member = memberService.getMember("test1").get();

        IntStream.rangeClosed(6, 10).forEach(i->{
            Novel novel = novelService.getNovel((long) i).get();
            Episode episode = novel.getEpisodes().get(0);
            RecentReadId recentReadId = new RecentReadId(member.getId(), novel.getId());
            recentReadService.createRecentRead(recentReadId, member, episode, novel);

        });
    }

    @Test
    @Transactional
    @Commit
    void makeLibraryItems() {
        favoriteNovelRepository.deleteAll();

        Random random = new Random();

        IntStream.rangeClosed(1, 50).forEach(i-> {
            long user = random.nextLong(1, 100);
            long novel = random.nextLong(1, 10);
            favoriteNovelService.toggleFavoriteNovel("test"+user, novel);
        });
    }

    @Test
    @Transactional
    @Commit
    void makeCoinChargeItems() {
        CoinChargeCreateDto build = CoinChargeCreateDto.builder()
                .providerId("test1")
                .coinAmount(100)
                .payment("5000.00")
                .build();
        coinChargeHistoryService.saveCoinChargeHistory(build);
    }
}