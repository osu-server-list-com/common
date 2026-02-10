package de.marcandreher.common.redis;

import java.util.Map;

public interface StreamMessageHandler {

    void handle(String messageId, Map<String, String> data) throws Exception;

}
