package org.enkrip.frappe.metadata.feedgroup;

import java.time.Instant;
import java.util.UUID;

import org.immutables.value.Value;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Value.Immutable
@Table("feed_groups_by_client")
public interface FeedGroupByClientEntity {
    @PrimaryKeyColumn(name = "client_id", type = PrimaryKeyType.PARTITIONED)
    UUID getClientId();

    @PrimaryKeyColumn(name = "feed_group_id", type = PrimaryKeyType.CLUSTERED)
    String getFeedGroupId();

    @PrimaryKeyColumn(name = "created_date", type = PrimaryKeyType.CLUSTERED)
    Instant getCreatedDate();
}
