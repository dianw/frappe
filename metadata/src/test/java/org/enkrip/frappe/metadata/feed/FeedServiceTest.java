package org.enkrip.frappe.metadata.feed;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.test.context.ActiveProfiles;

import com.google.protobuf.Empty;

import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FeedServiceImpl.class})
@ActiveProfiles({"test"})
class FeedServiceTest {

    @MockBean
    private ReactiveCassandraOperations reactiveCassandraOperations;
    @MockBean
    private FeedByFeedGroupRepository feedByFeedGroupRepository;
    @MockBean
    private FeedFollowerRepository feedFollowerRepository;
    @MockBean
    private FeedFollowingRepository feedFollowingRepository;

    @Autowired
    private FeedService feedService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findFeedByGroup() {
        when(feedByFeedGroupRepository.findByFeedGroupId(anyString(), anyString(), anyInt())).thenReturn(Flux.range(0, 5)
                .map(integer -> ImmutableFeedByFeedGroupEntity.builder()
                        .primaryKey(ImmutableFeedByFeedGroupPK.builder()
                                .feedGroupId(UUID.randomUUID().toString())
                                .feedId(UUID.randomUUID().toString())
                                .build())
                        .createdDate(Instant.now())
                        .build()
                ));

        Flux<FindFeedByGroupResponse> response = feedService
                .findFeedByGroup(FindFeedByGroupRequest.newBuilder().build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

        verify(feedByFeedGroupRepository).findByFeedGroupId(anyString(), anyString(), anyInt());
    }

    @Test
    void followFeed() {
        EntityWriteResult<FeedByFeedGroupEntity> mockResult = mock(EntityWriteResult.class);
        when(reactiveCassandraOperations.insert(any(FeedByFeedGroupEntity.class), any())).thenReturn(Mono.just(mockResult));
        when(feedFollowerRepository.insert(any(FeedFollowerEntity.class))).then(invocation -> Mono.just(invocation.getArgument(0)));
        when(feedFollowingRepository.insert(any(FeedFollowingEntity.class))).then(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Empty> response = feedService.followFeed(Flux.range(0, 5)
                .map(integer -> FollowFeedRequest
                        .newBuilder()
                        .setFeedIdSource(integer % 2 == 0 ? UUID.randomUUID().toString() : "")
                        .setFeedGroupIdSource(UUID.randomUUID().toString())
                        .setFeedIdTarget(UUID.randomUUID().toString())
                        .setFeedGroupIdTarget(UUID.randomUUID().toString())
                        .build()
                ), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .expectNext(Empty.getDefaultInstance())
                .verifyComplete();

        verify(reactiveCassandraOperations, times(6)).insert(any(), any());
        verify(feedFollowingRepository, times(3)).insert(any(FeedFollowingEntity.class));
        verify(feedFollowerRepository, times(3)).insert(any(FeedFollowerEntity.class));
    }

    @Test
    void unfollowFeed() {
        when(feedFollowerRepository.deleteByPK(anyString(), anyString(), anyString(), anyString())).thenReturn(Flux.empty());
        when(feedFollowingRepository.deleteByPK(anyString(), anyString(), anyString(), anyString())).thenReturn(Flux.empty());

        Mono<Empty> response = feedService.unfollowFeed(Flux.range(0, 5)
                .map(integer -> UnfollowFeedRequest
                        .newBuilder()
                        .setFeedIdSource(integer % 2 == 0 ? UUID.randomUUID().toString() : "")
                        .setFeedGroupIdSource(UUID.randomUUID().toString())
                        .setFeedIdTarget(UUID.randomUUID().toString())
                        .setFeedGroupIdTarget(UUID.randomUUID().toString())
                        .build()
                ), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .expectNext(Empty.getDefaultInstance())
                .verifyComplete();

        verify(feedFollowerRepository, times(3)).deleteByPK(anyString(), anyString(), anyString(), anyString());
        verify(feedFollowingRepository, times(3)).deleteByPK(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void findFeedsFollowers() {
        when(feedFollowerRepository.findByFeedGroupIdAndFeedId(anyString(), anyString())).thenReturn(Flux.range(0, 3)
                .map(integer -> ImmutableFeedFollowerEntity
                        .builder()
                        .feedGroupId(UUID.randomUUID().toString())
                        .feedId(UUID.randomUUID().toString())
                        .followerFeedGroupId(UUID.randomUUID().toString())
                        .followerFeedId(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .build()
                ));

        Flux<FindFeedsFollowersResponse> response = feedService.findFeedsFollowers(FindFeedsFollowersRequest.newBuilder()
                .setFeedGroupId(UUID.randomUUID().toString())
                .setFeedId(UUID.randomUUID().toString())
                .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();

        verify(feedFollowerRepository).findByFeedGroupIdAndFeedId(anyString(), anyString());
    }

    @Test
    void findFeedsFollowings() {
        when(feedFollowingRepository.findByFeedGroupIdAndFeedId(anyString(), anyString())).thenReturn(Flux.range(0, 3)
                .map(integer -> ImmutableFeedFollowingEntity
                        .builder()
                        .feedGroupId(UUID.randomUUID().toString())
                        .feedId(UUID.randomUUID().toString())
                        .followingFeedGroupId(UUID.randomUUID().toString())
                        .followingFeedId(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .build()
                ));

        Flux<FindFeedsFollowingsResponse> response = feedService.findFeedsFollowings(FindFeedsFollowingsRequest.newBuilder()
                .setFeedGroupId(UUID.randomUUID().toString())
                .setFeedId(UUID.randomUUID().toString())
                .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();

        verify(feedFollowingRepository).findByFeedGroupIdAndFeedId(anyString(), anyString());
    }
}