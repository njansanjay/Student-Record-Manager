
import com.sun.net.httpserver.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class StudentManager {
    static ArrayList<String> students = new ArrayList<>();

    static void send(HttpExchange ex, String text, String type)
            throws IOException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(200, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("localhost", 8081), 0);

        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();

            if (path.equals("/students")) {
                String method = ex.getRequestMethod();

                if (method.equals("POST")) {
                    String name = new String(
                        ex.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8).trim();

                    if (!name.isEmpty() && name.length() <= 100) {
                        students.add(name);
                    }
                }

                StringBuilder result = new StringBuilder("[");
                for (int i = 0; i < students.size(); i++) {
                    if (i > 0) result.append(",");
                    result.append("\"")
                          .append(students.get(i)
                              .replace("\\", "\\\\")
                              .replace("\"", "\\\""))
                          .append("\"");
                }
                result.append("]");
                send(ex, result.toString(), "application/json");
                return;
            }

            if (path.equals("/search")) {
                String query = ex.getRequestURI().getRawQuery();
                String term = query == null ? "" :
                    URLDecoder.decode(query.substring(
                        query.indexOf("=") + 1),
                        StandardCharsets.UTF_8);

                // Linear Search
                ArrayList<String> found = new ArrayList<>();
                for (String name : students) {
                    if (name.toLowerCase().contains(
                            term.toLowerCase())) {
                        found.add(name);
                    }
                }

                StringBuilder result = new StringBuilder("[");
                for (int i = 0; i < found.size(); i++) {
                    if (i > 0) result.append(",");
                    result.append("\"")
                          .append(found.get(i)
                              .replace("\\", "\\\\")
                              .replace("\"", "\\\""))
                          .append("\"");
                }
                result.append("]");
                send(ex, result.toString(), "application/json");
                return;
            }

            if (path.equals("/delete") &&
                    ex.getRequestMethod().equals("POST")) {
                String name = new String(
                    ex.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);

                students.remove(name);
                send(ex, "{\"success\":true}", "application/json");
                return;
            }

            String file = switch (path) {
                case "/" -> "index.html";
                case "/style.css" -> "style.css";
                case "/script.js" -> "script.js";
                default -> null;
            };

            if (file == null) {
                ex.sendResponseHeaders(404, -1);
                ex.close();
                return;
            }

            String type = file.endsWith(".html")
                ? "text/html; charset=UTF-8"
                : file.endsWith(".css")
                ? "text/css; charset=UTF-8"
                : "text/javascript; charset=UTF-8";

            send(ex, Files.readString(Path.of(file)), type);
        });

        server.start();
        System.out.println("Student Manager: http://localhost:8080");
    }
}
