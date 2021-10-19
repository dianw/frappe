package org.enkrip.frappe.metadata.feed;

import java.time.Instant;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {
        Instant.class
})
public interface FeedMapper {
    FeedMapper INSTANCE = Mappers.getMapper(FeedMapper.class);

    @Mapping(target = "primaryKey.feedGroupId", source = "request.feedGroupIdSource")
    @Mapping(target = "primaryKey.feedId", source = "request.feedIdSource")
    @Mapping(target = "createdDate", expression = "java( Instant.now() )")
    FeedByFeedGroupEntity toFeedByFeedGroupEntitySource(FollowFeedRequest request);

    @Mapping(target = "primaryKey.feedGroupId", source = "request.feedGroupIdTarget")
    @Mapping(target = "primaryKey.feedId", source = "request.feedIdTarget")
    @Mapping(target = "createdDate", expression = "java( Instant.now() )")
    FeedByFeedGroupEntity toFeedByFeedGroupEntityTarget(FollowFeedRequest request);

    @Mapping(target = "feedGroupId", source = "request.feedGroupIdSource")
    @Mapping(target = "feedId", source = "request.feedIdSource")
    @Mapping(target = "createdDate", expression = "java( Instant.now() )")
    @Mapping(target = "followingFeedGroupId", source = "request.feedGroupIdTarget")
    @Mapping(target = "followingFeedId", source = "request.feedIdTarget")
    FeedFollowingEntity toFeedFollowingEntity(FollowFeedRequest request);

    @Mapping(target = "feedGroupId", source = "request.feedGroupIdTarget")
    @Mapping(target = "feedId", source = "request.feedIdTarget")
    @Mapping(target = "createdDate", expression = "java( Instant.now() )")
    @Mapping(target = "followerFeedGroupId", source = "request.feedGroupIdSource")
    @Mapping(target = "followerFeedId", source = "request.feedIdSource")
    FeedFollowerEntity toFeedFollowerEntity(FollowFeedRequest request);

    @Mapping(target = "createdDateIso", source = "entity.createdDate")
    FindFeedsFollowersResponse toFindFeedFollowersResponse(FeedFollowerEntity entity);

    @Mapping(target = "createdDateIso", source = "entity.createdDate")
    FindFeedsFollowingsResponse toFindFeedFollowingsResponse(FeedFollowingEntity entity);

    @Mapping(target = "feedGroupId", source = "entity.primaryKey.feedGroupId")
    @Mapping(target = "feedId", source = "entity.primaryKey.feedId")
    @Mapping(target = "createdDateIso", source = "entity.createdDate")
    FindFeedByGroupResponse toFindFeedByGroupResponse(FeedByFeedGroupEntity entity);
}