package org.enkrip.frappe.metadata.feedgroup;


import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface FeedGroupRepository extends ReactiveCassandraRepository<ImmutableFeedGroupEntity, ImmutableFeedGroupId> {
    Flux<ImmutableFeedGroupEntity> findByFeedGroupId(String id);

    @Query("update feed_groups set script = ?1 where feed_group_id = ?0")
    void updateScript(String id, String script);
}