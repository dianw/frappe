package org.enkrip.frappe.metadata.feed;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface FeedFollowerRepository extends ReactiveCassandraRepository<FeedFollowerEntity, String> {
    @Query("delete from feed_followers_by_feed where feed_group_id = ?0 and feed_id = ?1" +
            " and follower_feed_group_id = ?3 and follower_feed_id = ?4")
    Flux<Void> deleteByPK(String feedGroupId, String feedId, String followerFeedGroupId, String followerId);

    Flux<FeedFollowerEntity> findByFeedGroupIdAndFeedId(String feedGroupId, String feedId);
}