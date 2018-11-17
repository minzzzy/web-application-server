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

            String url = HttpRequestUtils.parseUrl(line);

            int contentLength = 0;
            while (!"".equals(line)) {
                log.debug("header: {}", line);
                HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                if (pair != null && pair.getKey().equals("Content-Length")) {
                    contentLength = Integer.parseInt(pair.getValue());
                }
                line = bufferedReader.readLine();
            }

            String requestBody = IOUtils.readData(bufferedReader, contentLength);
            if (!requestBody.isEmpty()) {
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
                DataBase.addUser(new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email")));
            }

            log.debug("requestBody : {}", requestBody);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = getBody(HttpRequestUtils.getPath(url));
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
