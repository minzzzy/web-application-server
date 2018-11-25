package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponse {
    private static String homePath = "./webapp";
    private DataOutputStream dos;

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
    }

    public void forward(String path) throws IOException {
        byte[] body = getBody(path);
        response200Header(body.length, path);
        responseBody(body);
    }

    public void sendRedirect(String path) throws IOException {
        response302Header(path);
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
    }

    private void response200Header(int lengthOfBodyContent, String path) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes(getContentType(path));
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    public void addHeader(String key, String value) throws IOException {
        dos.writeBytes(key + ": " + value + "\r\n");
    }

    private void response302Header(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
        dos.writeBytes("\r\n");
    }

    private String getContentType(String path) {
        String fileExtension = path.replaceAll("^.*\\.(.*)$", "$1");
        if (fileExtension.equals("css")) {
            return "Content-Type: text/css\r\n";
        }
        return "Content-Type: text/html;charset=utf-8\r\n";
    }

    private byte[] getBody(String pathString) throws IOException {
        Path path = new File(homePath + pathString).toPath();
        if (path.toString().equals(homePath)) {
            return "Hello world".getBytes();
        }
        return Files.readAllBytes(path);
    }
}
