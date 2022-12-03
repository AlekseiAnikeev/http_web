package ru.agentche.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Aleksey Anikeev aka AgentChe
 * Date of creation: 02.12.2022
 */
public class Server {
    private final ExecutorService handlersExecutorService;
    private final ServerSocket serverSocket;

    private final List<String> validPaths = List.of(
            "/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js"
    );

    public Server(int port, int nThreads) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.handlersExecutorService = Executors.newFixedThreadPool(nThreads);
        serverRun();
    }

    public void serverRun() {
        System.out.println("Сервер запущен..."
                + "\nпорт: " + serverSocket.getLocalPort());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleConnection(Socket socket) {
        handlersExecutorService.execute(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                String requestLine = in.readLine();
                System.out.println(
                        "Получен запрос " + requestLine +
                                "\nНить -> " + Thread.currentThread().getName()
                );
                if (requestLine == null) {
                    return;
                }
                String[] parts = requestLine.split(" ");
                if (parts.length != 3) {
                    return;
                }

                String path = parts[1];
                if (!validPaths.contains(path)) {
                    out.write((
                            """
                                    HTTP/1.1 404 Not Found\r
                                    Content-Length: 0\r
                                    Connection: close\r
                                    \r
                                    """
                    ).getBytes());
                    out.flush();
                    return;
                }

                Path filePath = Path.of(".", "public", path);
                String mimeType = Files.probeContentType(filePath);
                if (path.equals("/classic.html")) {
                    String template = Files.readString(filePath);
                    byte[] content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                    return;
                }
                long length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
