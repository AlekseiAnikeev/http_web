package ru.agentche.http;

import java.io.BufferedOutputStream;
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Aleksey Anikeev aka AgentChe
 * Date of creation: 01.12.2022
 */

public class Main {
    public static void main(String[] args) {
        try {
            new Server(9999, 64);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

