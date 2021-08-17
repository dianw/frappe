package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;
import java.util.UUID;

import org.enkrip.frappe.metadata.group.CreateFeedGroupRequest;
import org.enkrip.frappe.metadata.group.CreateFeedGroupResponse;
import org.enkrip.frappe.metadata.group.FeedType;
import org.enkrip.frappe.metadata.group.GetFeedGroupsResponse;
import org.enkrip.frappe.metadata.group.UpdateFeedGroupScriptResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import reactor.core.publisher.Flux;

@Mapper(imports = {
        Instant.class,
        FeedType.class,
        UUID.class
})
public interface FeedGroupMapper {
    FeedGroupMapper INSTANCE = Mappers.getMapper(FeedGroupMapper.class);

    @Mapping(target = "primaryKey.id", source = "request.feedGroupId")
    @Mapping(target = "primaryKey.createdDate", expression = "java( Instant.now() )")
    @Mapping(target = "feedType", defaultExpression = "java( FeedType.FLAT.name() )")
    ImmutableFeedGroupEntity toFeedGroupEntity(CreateFeedGroupRequest request);

    @Mapping(target = "clientId", expression = "java( UUID.fromString(request.getClientId()) )")
    @Mapping(target = "createdDate", expression = "java( Instant.now() )")
    ImmutableFeedGroupByClientEntity toFeedGroupByClient(CreateFeedGroupRequest request);

    @Mapping(target = "feedGroupId", source = "entity.primaryKey.id")
    @Mapping(target = "createdDateIso", source = "entity.primaryKey.createdDate")
    CreateFeedGroupResponse toCreateFeedGroupResponse(FeedGroupEntity entity);

    @Mapping(target = "feedGroupId", source = "entity.primaryKey.id")
    UpdateFeedGroupScriptResponse toUpdateFeedGroupScriptResponse(FeedGroupEntity entity);

    default GetFeedGroupsResponse toGetFeedGroupResponse(String clientId, Flux<ImmutableFeedGroupByClientEntity> entities) {
        return GetFeedGroupsResponse.newBuilder()
                .setClientId(clientId)
                .addAllFeedGroup(entities.map(this::toFeedGroup).toIterable())
                .build();
    }

    @Mapping(target = "createdDateIso", source = "entity.createdDate")
    GetFeedGroupsResponse.FeedGroup toFeedGroup(ImmutableFeedGroupByClientEntity entity);
}