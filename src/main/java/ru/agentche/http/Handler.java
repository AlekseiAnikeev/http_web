package ru.agentche.http;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @author Aleksey Anikeev aka AgentChe
 * Date of creation: 03.12.2022
 */

@FunctionalInterface
public interface Handler {
    void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}