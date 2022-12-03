package ru.agentche.http;

/**
 * @author Aleksey Anikeev aka AgentChe
 * Date of creation: 03.12.2022
 */
public record Response(int code, String description, String contentType, long contentLength) {
}