package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ControllerMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);
            ControllerMapper controllerMapper = new ControllerMapper();

            Controller controller = controllerMapper.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
