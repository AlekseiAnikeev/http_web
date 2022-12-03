package ru.agentche.http;

/**
 * @author Aleksey Anikeev aka AgentChe
 * Date of creation: 03.12.2022
 */
public record Request(String method, String path, String headers, String body) {
}