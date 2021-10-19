package org.enkrip.frappe.metadata.feed;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface FeedByFeedGroupRepository extends ReactiveCassandraRepository<ImmutableFeedByFeedGroupEntity, ImmutableFeedByFeedGroupPK> {
    @Query("select * from feeds_by_feed_group where feed_group_id = ?0 and feed_id > ?1 limit ?3")
    Flux<ImmutableFeedByFeedGroupEntity> findByFeedGroupId(String feedGroupId, String feedIdGt, int limit);
}