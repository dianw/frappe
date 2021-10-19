package org.enkrip.frappe.metadata.feed;

import java.time.Instant;

import org.immutables.value.Value;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Value.Immutable
@Table("feeds_by_feed_group")
public interface FeedByFeedGroupEntity {
    @PrimaryKey
    ImmutableFeedByFeedGroupPK getPrimaryKey();

    Instant getCreatedDate();

    @Value.Immutable
    @PrimaryKeyClass
    interface FeedByFeedGroupPK {
        @PrimaryKeyColumn(name = "feed_group_id", type = PrimaryKeyType.PARTITIONED)
        String getFeedGroupId();

        @PrimaryKeyColumn(name = "feed_id", type = PrimaryKeyType.CLUSTERED)
        String getFeedId();
    }
}
