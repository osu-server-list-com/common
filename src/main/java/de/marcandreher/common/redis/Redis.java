package de.marcandreher.common.redis;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;

import de.marcandreher.fusionkit.core.FusionKit;
import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.resps.StreamEntry;

public class Redis {

    private final static Logger logger = FusionKit.getLogger(Redis.class);

    public static RedisClient connect(Dotenv dotenv) {
        RedisClient redisClient = RedisClient.builder().hostAndPort(dotenv.get("REDIS_HOST"), Integer.parseInt(dotenv.get("REDIS_PORT"))).build();

        String ping = redisClient.ping();
        if(!"PONG".equals(ping)) {
            logger.error("Failed to connect to Redis: PING response was {}", ping);
            System.exit(1);
        }

        logger.info("Connected to redis at: {}:{}", dotenv.get("REDIS_HOST"), Integer.parseInt(dotenv.get("REDIS_PORT")));
        return redisClient;
    }

    public static Map<String, String> sendAndWait(String stream, Map<String, String> payload, Duration timeout, RedisClient client) {
        String correlationId = UUID.randomUUID().toString();
        String responseStream = "response:" + correlationId;

        payload.put("correlationId", correlationId);
        payload.put("replyTo", responseStream);

        client.xadd(stream, StreamEntryID.NEW_ENTRY, payload);

        try {
            List<Map.Entry<String, List<StreamEntry>>> response =
                client.xread(
                        new XReadParams().count(1).block((int)timeout.toMillis()),
                        Map.of(responseStream, new StreamEntryID(0L, 0L))
                );

            if (response == null) {
                throw new RuntimeException("Timeout waiting for response");
            }

            return response.get(0).getValue().get(0).getFields();
        } finally {
            // Cleanup: delete the temporary response stream
            try {
                client.del(responseStream);
            } catch (Exception e) {
                logger.warn("Failed to cleanup response stream: {}", responseStream, e);
            }
        }
    }

    public static void reply(String streamName, Map<String, String> data, RedisClient client) {
        client.xadd(streamName, StreamEntryID.NEW_ENTRY, data);
    }
}
