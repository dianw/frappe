package org.enkrip.frappe.metadata.feed;

import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeedServiceImpl implements FeedService {
    private static final InsertOptions INSERT_IF_NOT_EXISTS = InsertOptions.builder().withIfNotExists().build();

    private final FeedMapper feedMapper = FeedMapper.INSTANCE;
    private final FeedByFeedGroupRepository feedByFeedGroupRepository;
    private final FeedFollowingRepository feedFollowingRepository;
    private final FeedFollowerRepository feedFollowerRepository;
    private final ReactiveCassandraOperations cassandraOperations;

    public FeedServiceImpl(FeedByFeedGroupRepository feedByFeedGroupRepository, FeedFollowingRepository feedFollowingRepository, FeedFollowerRepository feedFollowerRepository, ReactiveCassandraOperations cassandraOperations) {
        this.feedByFeedGroupRepository = feedByFeedGroupRepository;
        this.feedFollowingRepository = feedFollowingRepository;
        this.feedFollowerRepository = feedFollowerRepository;
        this.cassandraOperations = cassandraOperations;
    }

    @Override
    public Flux<FindFeedByGroupResponse> findFeedByGroup(FindFeedByGroupRequest message, ByteBuf metadata) {
        return feedByFeedGroupRepository.findByFeedGroupId(message.getFeedRoupId(), message.getFeedIdGt(), message.getLimit())
                .map(feedMapper::toFindFeedByGroupResponse);
    }

    @Override
    public Mono<Empty> followFeed(Publisher<FollowFeedRequest> messages, ByteBuf metadata) {
        return Flux.from(messages)
                .filter(message -> StringUtils.isNotBlank(message.getFeedIdSource()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedGroupIdSource()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedIdTarget()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedGroupIdTarget()))
                .flatMap(message -> {
                    FeedByFeedGroupEntity source = feedMapper.toFeedByFeedGroupEntitySource(message);
                    Mono<EntityWriteResult<FeedByFeedGroupEntity>> sourceResult = cassandraOperations.insert(source, INSERT_IF_NOT_EXISTS);

                    FeedByFeedGroupEntity target = feedMapper.toFeedByFeedGroupEntityTarget(message);
                    Mono<EntityWriteResult<FeedByFeedGroupEntity>> targetResult = cassandraOperations.insert(target, INSERT_IF_NOT_EXISTS);

                    return Mono.zip(sourceResult, targetResult).then(Mono.just(message));
                })
                .flatMap(message -> {
                    Mono<FeedFollowingEntity> followingResult = feedFollowingRepository.insert(feedMapper.toFeedFollowingEntity(message));
                    Mono<FeedFollowerEntity> followerResult = feedFollowerRepository.insert(feedMapper.toFeedFollowerEntity(message));

                    return Mono.zip(followerResult, followingResult).then(Mono.just(message));
                })
                .then(Mono.just(Empty.getDefaultInstance()));
    }

    @Override
    public Mono<Empty> unfollowFeed(Publisher<UnfollowFeedRequest> messages, ByteBuf metadata) {
        return Flux.from(messages)
                .filter(message -> StringUtils.isNotBlank(message.getFeedIdSource()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedGroupIdSource()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedIdTarget()))
                .filter(message -> StringUtils.isNotBlank(message.getFeedGroupIdTarget()))
                .flatMap(message -> {
                    Flux<Void> followingResult = feedFollowingRepository.deleteByPK(message.getFeedGroupIdSource(), message.getFeedIdSource(),
                            message.getFeedGroupIdTarget(), message.getFeedIdTarget());
                    Flux<Void> followerResult = feedFollowerRepository.deleteByPK(message.getFeedGroupIdTarget(), message.getFeedIdTarget(),
                            message.getFeedGroupIdSource(), message.getFeedIdSource());

                    return Flux.merge(followingResult, followerResult).then(Mono.just(message));
                })
                .then(Mono.just(Empty.getDefaultInstance()));
    }

    @Override
    public Flux<FindFeedsFollowersResponse> findFeedsFollowers(FindFeedsFollowersRequest message, ByteBuf metadata) {
        return feedFollowerRepository.findByFeedGroupIdAndFeedId(message.getFeedGroupId(), message.getFeedId())
                .map(feedMapper::toFindFeedFollowersResponse);
    }

    @Override
    public Flux<FindFeedsFollowingsResponse> findFeedsFollowings(FindFeedsFollowingsRequest message, ByteBuf metadata) {
        return feedFollowingRepository.findByFeedGroupIdAndFeedId(message.getFeedGroupId(), message.getFeedId())
                .map(feedMapper::toFindFeedFollowingsResponse);
    }
}
