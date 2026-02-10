package de.marcandreher.common.redis;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.RedisClient;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;

public class RedisStreamConsumerImpl implements RedisStreamConsumer, Runnable {

    private final RedisClient jedis;
    private final String streamName;
    private final String groupName;
    private final String consumerName;
    private final StreamMessageHandler handler;

    private volatile boolean running = true;
    private Thread workerThread;

    public RedisStreamConsumerImpl(RedisClient jedis,
                                   String streamName,
                                   String groupName,
                                   String consumerName,
                                   StreamMessageHandler handler) {
        this.jedis = jedis;
        this.streamName = streamName;
        this.groupName = groupName;
        this.consumerName = consumerName;
        this.handler = handler;

        createGroupIfNotExists();
    }

    @Override
    public void start() {
        workerThread = new Thread(this, "redis-stream-consumer-" + consumerName);
        workerThread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {

                // First process pending messages (in case of crash recovery)
                readMessages(StreamEntryID.XREADGROUP_UNDELIVERED_ENTRY);

            } catch (Exception e) {
                System.err.println("Consumer loop error:");
                e.printStackTrace();

                try {
                    Thread.sleep(1000); // small backoff
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void readMessages(StreamEntryID offset) {
        List<Map.Entry<String, List<StreamEntry>>> messages =
                jedis.xreadGroup(
                        groupName,
                        consumerName,
                        XReadGroupParams.xReadGroupParams()
                                .count(10)
                                .block(5 * 60),
                        Map.of(streamName, offset)
                );

        if (messages == null) return;

        for (Map.Entry<String, List<StreamEntry>> stream : messages) {
            for (StreamEntry entry : stream.getValue()) {
                processEntry(entry);
            }
        }
    }

    private void processEntry(StreamEntry entry) {
        try {
            handler.handle(entry.getID().toString(), entry.getFields());
            jedis.xack(streamName, groupName, entry.getID());
        } catch (Exception e) {
            System.err.println("Failed processing message: " + entry.getID());
            e.printStackTrace();
            // message remains pending → can be retried
        }
    }

    private void createGroupIfNotExists() {
        try {
            // MKSTREAM flag = true creates stream if it doesn't exist
            jedis.xgroupCreate(streamName, groupName, new StreamEntryID("0"), true);
            System.out.println("Created consumer group: " + groupName + " on stream: " + streamName);
        } catch (Exception e) {
            // Only ignore if group already exists, otherwise log the error
            if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
                System.out.println("Consumer group already exists: " + groupName);
            } else {
                System.err.println("Failed to create consumer group: " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }
}
