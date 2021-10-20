package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;
import java.util.UUID;

import org.enkrip.frappe.metadata.group.CreateFeedGroupRequest;
import org.enkrip.frappe.metadata.group.CreateFeedGroupResponse;
import org.enkrip.frappe.metadata.group.DeleteFeedGroupRequest;
import org.enkrip.frappe.metadata.group.FeedGroupService;
import org.enkrip.frappe.metadata.group.FeedType;
import org.enkrip.frappe.metadata.group.GetFeedGroupsRequest;
import org.enkrip.frappe.metadata.group.GetFeedGroupsResponse;
import org.enkrip.frappe.metadata.group.UpdateFeedGroupScriptRequest;
import org.enkrip.frappe.metadata.group.UpdateFeedGroupScriptResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.google.protobuf.Empty;

import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FeedGroupServiceImpl.class})
@ActiveProfiles({"test"})
class FeedGroupServiceTest {
    @MockBean
    private FeedGroupRepository feedGroupRepository;
    @MockBean
    private FeedGroupByClientRepository feedGroupByClientRepository;
    @Autowired
    private FeedGroupService feedGroupService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createFeedGroup() {
        when(feedGroupRepository.insert(any(ImmutableFeedGroupEntity.class)))
                .then(invocation -> Mono.just(invocation.getArgument(0)));
        when(feedGroupByClientRepository.insert(any(ImmutableFeedGroupByClientEntity.class)))
                .then(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<CreateFeedGroupResponse> response = feedGroupService.createFeedGroup(CreateFeedGroupRequest.newBuilder()
                .setFeedGroupId("testing")
                .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(response)
                .expectSubscription()
                .assertNext(createFeedGroupResponse -> {
                    assertThat(createFeedGroupResponse.getFeedGroupId()).isEqualTo("testing");
                    assertThat(createFeedGroupResponse.getFeedType()).isEqualTo(FeedType.FLAT);
                    assertThat(createFeedGroupResponse.getCreatedDateIso()).isNotBlank();
                })
                .then(() -> {
                    verify(feedGroupRepository).insert(any(ImmutableFeedGroupEntity.class));

                    ArgumentCaptor<ImmutableFeedGroupByClientEntity> insertFeedGroupByClientArgs =
                            ArgumentCaptor.forClass(ImmutableFeedGroupByClientEntity.class);
                    verify(feedGroupByClientRepository).insert(insertFeedGroupByClientArgs.capture());
                    assertThat(insertFeedGroupByClientArgs.getValue().getClientId())
                            .asString()
                            .isEqualTo(FeedGroupServiceImpl.DEFAULT_CLIENT_ID);
                })
                .verifyComplete();
    }

    @Test
    void updateFeedGroupScript() {
        doNothing().when(feedGroupRepository).updateScript(anyString(), anyString());
        when(feedGroupRepository.findByPrimaryKeyId(anyString()))
                .then(invocation -> Flux.just(ImmutableFeedGroupEntity.builder()
                        .primaryKey(ImmutableFeedGroupId.builder()
                                .id(invocation.getArgument(0))
                                .createdDate(Instant.now())
                                .build())
                        .feedType("FLAT")
                        .script("resultTestScript")
                        .build()));


        Mono<UpdateFeedGroupScriptResponse> response =
                feedGroupService.updateFeedGroupScript(UpdateFeedGroupScriptRequest
                        .newBuilder()
                        .setFeedGroupId("test")
                        .setScript("testScript")
                        .build(), Unpooled.EMPTY_BUFFER);
        StepVerifier.create(response)
                .expectSubscription()
                .assertNext(feedGroup -> {
                    assertThat(feedGroup.getFeedGroupId()).isEqualTo("test");
                    assertThat(feedGroup.getScript()).isEqualTo("resultTestScript");
                })
                .then(() -> {
                    verify(feedGroupRepository).updateScript(eq("test"), eq("testScript"));
                    verify(feedGroupRepository).findByPrimaryKeyId("test");
                })
                .verifyComplete();
    }

    @Test
    void getFeedGroups() {
        UUID clientId = Uuids.timeBased();

        Flux<ImmutableFeedGroupByClientEntity> mockResult = Flux.range(0, 5)
                .map(i -> ImmutableFeedGroupByClientEntity.builder()
                        .clientId(clientId)
                        .feedGroupId(Uuids.timeBased().toString())
                        .createdDate(Instant.now())
                        .build());

        when(feedGroupByClientRepository.findByClientIdCreatedDateAfter(any(), any(), anyInt())).thenReturn(mockResult);
        when(feedGroupByClientRepository.findByClientIdCreatedDateBefore(any(), any(), anyInt())).thenReturn(mockResult);
        when(feedGroupByClientRepository.findByClientId(any(), anyInt())).thenReturn(mockResult);

        Mono<GetFeedGroupsResponse> findCreateDateAfterResponse = feedGroupService
                .getFeedGroups(GetFeedGroupsRequest.newBuilder()
                        .setClientId(clientId.toString())
                        .setCreatedDateIsoAfter(Instant.now().toString())
                        .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(findCreateDateAfterResponse)
                .expectSubscription()
                .assertNext(getFeedGroupsResponse -> {
                    assertThat(getFeedGroupsResponse.getClientId()).isEqualTo(clientId.toString());
                    assertThat(getFeedGroupsResponse.getFeedGroupCount()).isEqualTo(5);
                    verify(feedGroupByClientRepository).findByClientIdCreatedDateAfter(any(), any(), anyInt());
                    verify(feedGroupByClientRepository, never()).findByClientIdCreatedDateBefore(any(), any(), anyInt());
                    verify(feedGroupByClientRepository, never()).findByClientId(any(), anyInt());
                })
                .verifyComplete();

        Mono<GetFeedGroupsResponse> findCreateDateBeforeResponse = feedGroupService
                .getFeedGroups(GetFeedGroupsRequest.newBuilder()
                        .setClientId(clientId.toString())
                        .setCreatedDateIsoBefore(Instant.now().toString())
                        .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(findCreateDateBeforeResponse)
                .expectSubscription()
                .assertNext(getFeedGroupsResponse -> {
                    assertThat(getFeedGroupsResponse.getClientId()).isEqualTo(clientId.toString());
                    assertThat(getFeedGroupsResponse.getFeedGroupCount()).isEqualTo(5);
                    verify(feedGroupByClientRepository).findByClientIdCreatedDateAfter(any(), any(), anyInt());
                    verify(feedGroupByClientRepository).findByClientIdCreatedDateBefore(any(), any(), anyInt());
                    verify(feedGroupByClientRepository, never()).findByClientId(any(), anyInt());
                })
                .verifyComplete();

        Mono<GetFeedGroupsResponse> findResponse = feedGroupService
                .getFeedGroups(GetFeedGroupsRequest.newBuilder()
                        .setClientId(clientId.toString())
                        .build(), Unpooled.EMPTY_BUFFER);

        StepVerifier.create(findResponse)
                .expectSubscription()
                .assertNext(getFeedGroupsResponse -> {
                    assertThat(getFeedGroupsResponse.getClientId()).isEqualTo(clientId.toString());
                    assertThat(getFeedGroupsResponse.getFeedGroupCount()).isEqualTo(5);
                    verify(feedGroupByClientRepository).findByClientIdCreatedDateAfter(any(), any(), anyInt());
                    verify(feedGroupByClientRepository).findByClientIdCreatedDateBefore(any(), any(), anyInt());
                    verify(feedGroupByClientRepository).findByClientId(any(), anyInt());
                })
                .verifyComplete();
    }

    @Test
    void deleteFeedGroup() {
        Mono<Empty> response = feedGroupService.deleteFeedGroup(DeleteFeedGroupRequest.newBuilder().build(), Unpooled.EMPTY_BUFFER);
        StepVerifier.create(response)
                .verifyError(UnsupportedOperationException.class);
    }
}