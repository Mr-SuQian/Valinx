package com.valinx.kernel.event;

import com.fasterxml.jackson.databind.JsonNode;

public interface EventHandler {
    void handle(JsonNode event);
}
