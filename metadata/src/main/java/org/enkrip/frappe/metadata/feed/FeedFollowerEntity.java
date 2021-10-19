package org.enkrip.frappe.metadata.feed;

import java.time.Instant;

import org.immutables.value.Value;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Value.Immutable
@Table("feed_followers_by_feed")
public interface FeedFollowerEntity {
    @PrimaryKeyColumn(name = "feed_group_id", type = PrimaryKeyType.PARTITIONED)
    String getFeedGroupId();

    @PrimaryKeyColumn(name = "feed_id", type = PrimaryKeyType.PARTITIONED)
    String getFeedId();

    @PrimaryKeyColumn(name = "follower_feed_group_id", type = PrimaryKeyType.CLUSTERED)
    String followerFeedGroupId();

    @PrimaryKeyColumn(name = "follower_feed_id", type = PrimaryKeyType.CLUSTERED)
    String followerFeedId();

    @Column("created_date")
    Instant getCreatedDate();
}