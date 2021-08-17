package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface FeedGroupByClientRepository extends ReactiveCassandraRepository<ImmutableFeedGroupByClientEntity, UUID> {
    @Query("select * from feed_groups_by_client where client_id = ?0 limit ?1")
    Flux<ImmutableFeedGroupByClientEntity> findByClientId(UUID clientId, int limit);

    @Query("select * from feed_groups_by_client where client_id = ?0 and created_date > ?1 limit ?2")
    Flux<ImmutableFeedGroupByClientEntity> findByClientIdCreatedDateAfter(UUID clientId, Instant createdDate, int limit);

    @Query("select * from feed_groups_by_client where client_id = ?0 and created_date < ?1 limit ?2")
    Flux<ImmutableFeedGroupByClientEntity> findByClientIdCreatedDateBefore(UUID clientId, Instant createdDate, int limit);
}