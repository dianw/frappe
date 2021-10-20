package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.enkrip.frappe.metadata.group.CreateFeedGroupRequest;
import org.enkrip.frappe.metadata.group.CreateFeedGroupResponse;
import org.enkrip.frappe.metadata.group.DeleteFeedGroupRequest;
import org.enkrip.frappe.metadata.group.FeedGroupService;
import org.enkrip.frappe.metadata.group.GetFeedGroupsRequest;
import org.enkrip.frappe.metadata.group.GetFeedGroupsResponse;
import org.enkrip.frappe.metadata.group.UpdateFeedGroupScriptRequest;
import org.enkrip.frappe.metadata.group.UpdateFeedGroupScriptResponse;
import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FeedGroupServiceImpl implements FeedGroupService {
    static final String DEFAULT_CLIENT_ID = "7e614ea0-e266-11eb-b611-b1eb76e8ee3f";

    private final FeedGroupMapper feedGroupMapper = FeedGroupMapper.INSTANCE;
    private final FeedGroupRepository feedGroupRepository;
    private final FeedGroupByClientRepository feedGroupByClientRepository;

    public FeedGroupServiceImpl(FeedGroupRepository feedGroupRepository, FeedGroupByClientRepository feedGroupByClientRepository) {
        this.feedGroupRepository = feedGroupRepository;
        this.feedGroupByClientRepository = feedGroupByClientRepository;
    }

    @Override
    public Mono<CreateFeedGroupResponse> createFeedGroup(CreateFeedGroupRequest message, ByteBuf metadata) {
        final Mono<FeedGroupByClientEntity> insertFeedGroupByClient = insertFeedGroupByClient(message);
        final Mono<FeedGroupEntity> insertFeedGroup = insertFeedGroup(message);

        return insertFeedGroupByClient
                .then(insertFeedGroup)
                .map(feedGroupMapper::toCreateFeedGroupResponse);
    }

    @Override
    public Mono<UpdateFeedGroupScriptResponse> updateFeedGroupScript(UpdateFeedGroupScriptRequest message, ByteBuf metadata) {
        return Mono.just(message)
                .doOnNext(request -> feedGroupRepository.updateScript(request.getFeedGroupId(), request.getScript()))
                .flatMap(request -> feedGroupRepository.findByPrimaryKeyId(request.getFeedGroupId()).next())
                .map(feedGroupMapper::toUpdateFeedGroupScriptResponse);
    }

    @Override
    public Mono<GetFeedGroupsResponse> getFeedGroups(GetFeedGroupsRequest message, ByteBuf metadata) {
        UUID clientId = UUID.fromString(getClientIdOrDefault(message.getClientId()));

        Mono<GetFeedGroupsRequest> messageMono = Mono.just(message);

        Flux<ImmutableFeedGroupByClientEntity> findByCreatedDateLtResult = messageMono
                .filter(request -> StringUtils.isNotBlank(message.getCreatedDateIsoBefore()))
                .flatMapMany(request -> feedGroupByClientRepository
                        .findByClientIdCreatedDateBefore(clientId, Instant.parse(message.getCreatedDateIsoBefore()), message.getLimit()));

        Flux<ImmutableFeedGroupByClientEntity> findByCreatedDateGtResult = messageMono
                .filter(request -> StringUtils.isNotBlank(message.getCreatedDateIsoAfter()))
                .flatMapMany(request -> feedGroupByClientRepository
                        .findByClientIdCreatedDateAfter(clientId, Instant.parse(message.getCreatedDateIsoAfter()), message.getLimit()));

        Flux<ImmutableFeedGroupByClientEntity> findAllResult = messageMono
                .filter(request -> StringUtils.isBlank(message.getCreatedDateIsoBefore()))
                .filter(request -> StringUtils.isBlank(message.getCreatedDateIsoAfter()))
                .flatMapMany(request -> feedGroupByClientRepository.findByClientId(clientId, request.getLimit()));

        Flux<ImmutableFeedGroupByClientEntity> result = Flux.merge(findByCreatedDateLtResult, findByCreatedDateGtResult, findAllResult);

        return Mono.just(feedGroupMapper.toGetFeedGroupResponse(clientId.toString(), result));
    }

    @Override
    public Mono<Empty> deleteFeedGroup(DeleteFeedGroupRequest message, ByteBuf metadata) {
        return Mono.error(new UnsupportedOperationException());
    }

    private Mono<FeedGroupEntity> insertFeedGroup(CreateFeedGroupRequest message) {
        return Mono.just(message)
                .map(feedGroupMapper::toFeedGroupEntity)
                .flatMap(feedGroupRepository::insert);
    }

    private Mono<FeedGroupByClientEntity> insertFeedGroupByClient(CreateFeedGroupRequest message) {
        return Mono.just(message)
                .map(request -> request.toBuilder()
                        .setClientId(getClientIdOrDefault(request.getClientId()))
                        .build()
                )
                .map(feedGroupMapper::toFeedGroupByClient)
                .flatMap(feedGroupByClientRepository::insert);
    }

    private String getClientIdOrDefault(String clientId) {
        return Optional.ofNullable(clientId)
                .filter(StringUtils::isNotBlank)
                .orElse(DEFAULT_CLIENT_ID);
    }
}
