package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Value.Immutable
@Table("feed_groups")
public interface FeedGroupEntity {
    @PrimaryKey
    ImmutableFeedGroupId getPrimaryKey();

    @Column("feed_type")
    String getFeedType();

    @Nullable
    @Column
    String getScript();

    @Value.Immutable
    @PrimaryKeyClass
    interface FeedGroupId {
        @PrimaryKeyColumn(name = "feed_group_id", type = PrimaryKeyType.PARTITIONED)
        String getId();

        @PrimaryKeyColumn(name = "created_date", type = PrimaryKeyType.CLUSTERED)
        Instant getCreatedDate();
    }
}