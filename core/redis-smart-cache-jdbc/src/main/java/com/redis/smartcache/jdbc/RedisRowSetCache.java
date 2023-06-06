package com.redis.smartcache.jdbc;

import java.sql.SQLException;

import javax.sql.RowSet;

import com.redis.lettucemod.util.RedisModulesUtils;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;

public class RedisRowSetCache implements RowSetCache {

	private final StatefulRedisConnection<String, RowSet> connection;

	public RedisRowSetCache(AbstractRedisClient client, RedisCodec<String, RowSet> codec) {
		this.connection = RedisModulesUtils.connection(client, codec);
	}

	@Override
	public RowSet get(String key) {
		return connection.sync().get(key);
	}

	@Override
	public void put(String key, RowSet rowSet, long ttlMillis) throws SQLException {
		if (ttlMillis > 0) {
			connection.sync().psetex(key, ttlMillis, rowSet);
		} else {
			connection.sync().set(key, rowSet);
		}
	}

	@Override
	public void close() {
		connection.close();
	}

}
