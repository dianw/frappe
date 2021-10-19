package org.enkrip.frappe.metadata.feed;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface FeedFollowingRepository extends ReactiveCassandraRepository<FeedFollowingEntity, String> {
    @Query("delete from feed_followers_by_feed where feed_group_id = ?0 and feed_id = ?1" +
            " and following_feed_group_id = ?3 and following_feed_id = ?4")
    Flux<Void> deleteByPK(String feedGroupId, String feedId, String followingFeedGroupId, String followingId);

    Flux<FeedFollowingEntity> findByFeedGroupIdAndFeedId(String feedGroupId, String feedId);
}