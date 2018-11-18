package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String HOME_PATH = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            log.debug("header : {}", line);

            HttpRequestUtils.StartLine startLine = HttpRequestUtils.parseStartLine(line);

            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = null;
            String path = HttpRequestUtils.getPath(startLine.getUrl());
            if (path.equals("/user/create")) {
                String requestBody = IOUtils.readData(bufferedReader, findContentLength(getHeader(bufferedReader)));
                saveUser(getParams(requestBody));
                log.debug("requestBody : {}", requestBody);

                String redirectPath = "/index.html";
                body = getBody(redirectPath);
                response302Header(dos, redirectPath, false);
            } else if (path.equals("/user/login")) {
                String requestBody = IOUtils.readData(bufferedReader, findContentLength(getHeader(bufferedReader)));

                boolean logined = isLoginUser(getParams(requestBody));
                String redirectPath = null;
                if (logined) {
                    redirectPath = "/index.html";
                } else {
                    redirectPath = "/user/login_failed.html";
                }
                body = getBody(redirectPath);
                response302Header(dos, redirectPath, logined);
            } else {
                body = getBody(path);
                response200Header(dos, body.length);
            }
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, String> getParams(String requestBody) {
        if (requestBody.isEmpty()) {
            return null;
        }
        return HttpRequestUtils.parseQueryString(requestBody);
    }

    private void saveUser(Map<String, String> params) {
        if (!params.containsKey("userId") || !params.containsKey("password") || !params.containsKey("name") || !params.containsKey("email")) {
            return;
        }
        DataBase.addUser(new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email")));
        log.debug("Save user: {}", DataBase.findUserById(params.get("userId")));
    }

    private boolean isLoginUser(Map<String, String> params) {
        User user = DataBase.findUserById(params.get("userId"));
        if (user != null && user.getPassword().equals(params.get("password"))) {
            return true;
        }
        return false;
    }

    private ArrayList<String> getHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        ArrayList<String> lines = new ArrayList<>();
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            log.debug("header: {}", line);
            lines.add(line);
        }
        return lines;
    }

    private int findContentLength(ArrayList<String> lines) {
        return lines.stream().map(HttpRequestUtils::parseHeader)
                .filter(pair -> pair != null && pair.getKey().equals("Content-Length"))
                .map(pair -> Integer.parseInt(pair.getValue()))
                .findAny()
                .orElse(0);
    }

    private byte[] getBody(String url) throws IOException {
        Path path = new File(HOME_PATH + url).toPath();
        if (path.toString().equals(HOME_PATH)) {
            return "Hello world".getBytes();
        }
        return Files.readAllBytes(path);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location, boolean isLogined) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            if (isLogined) {
                dos.writeBytes("Set-Cookie: logined=true\r\n");
            } else {
                dos.writeBytes("Set-Cookie: logined=false\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
