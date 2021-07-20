CREATE TYPE IF NOT EXISTS metadata.activity (
    verb text,
    actor_id text,
    other frozen<map<text, text>>
);

CREATE TABLE IF NOT EXISTS metadata.feed_groups_by_client (
    client_id timeuuid,
    feed_group_id text,
    created_date timestamp,
    PRIMARY KEY ((client_id), created_date, feed_group_id)
) WITH CLUSTERING ORDER BY (created_date DESC);

CREATE TABLE IF NOT EXISTS metadata.feed_groups (
    feed_group_id text,
    feed_type text,
    script text,
    created_date timestamp,
    PRIMARY KEY ((feed_group_id), created_date)
) WITH CLUSTERING ORDER BY (created_date DESC);

CREATE TABLE IF NOT EXISTS metadata.feeds_by_feed_group (
    feed_group_id text,
    feed_id text,
    created_date timestamp,
    PRIMARY KEY ((feed_group_id), feed_id, created_date)
) WITH CLUSTERING ORDER BY (feed_id DESC, created_date DESC);

CREATE TABLE IF NOT EXISTS metadata.activities_by_feed (
    feed_group_id text,
    feed_id text,
    activity_id timeuuid,
    activity frozen<activity>,
    created_date timestamp,
    PRIMARY KEY ((feed_group_id, feed_id), activity_id)
) WITH CLUSTERING ORDER BY (activity_id DESC);

CREATE TABLE IF NOT EXISTS metadata.feed_followers_by_feed (
    feed_group_id text,
    feed_id text,
    follower_feed_group_id text,
    follower_feed_id text,
    created_date timestamp,
    PRIMARY KEY ((feed_group_id, feed_id), created_date)
);

CREATE TABLE IF NOT EXISTS metadata.feed_followings_by_feed (
    feed_group_id text,
    feed_id text,
    following_feed_group_id text,
    following_feed_id text,
    created_date timestamp,
    PRIMARY KEY ((feed_group_id, feed_id), created_date)
);