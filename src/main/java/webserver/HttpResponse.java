package webserver;

import db.DataBase;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class HttpResponse {
    private static String homePath = "./webapp";
    private DataOutputStream dos;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
    }

    public void forward(String path, String accept) throws IOException {
        byte[] body = getBody(path);
        response200Header(body.length, accept);
        responseBody(body);
    }

    public void sendRedirect(String path) throws IOException {
        dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
        dos.writeBytes("Location: " + path + "\r\n");
        addResponseHeader();
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
    }

    private void response200Header(int lengthOfBodyContent, String accept) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes(String.format("Content-Type: %s\r\n", accept));
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void addResponseHeader() throws IOException {
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
        }
    }

    private byte[] getBody(String pathString) throws IOException {
        if (pathString.equals("/user/list")) {
            return UserListHtml.get(DataBase.findAll()).getBytes();
        }
        Path path = new File(homePath + pathString).toPath();
        if (path.toString().equals(homePath)) {
            return "Hello world".getBytes();
        }
        return Files.readAllBytes(path);
    }

    public void addCookie(String cookie) {
        if (!Pattern.matches("(.*)=(.*)", cookie)) {
            return;
        }
        String value = headers.get("Set-Cookie");
        if (value == null) {
            headers.put("Set-Cookie", cookie);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String cookieValue = stringBuilder.append(value).append("; ").append(cookie).toString();
        headers.put("Set-Cookie", cookieValue);
    }
}
