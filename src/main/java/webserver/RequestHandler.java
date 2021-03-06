package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ControllerMapper;
import webserver.controller.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            if (httpRequest.getCookies().getCookie("JSESSIONID") == null) {
                httpResponse.addCookie(String.format("JSESSIONID=%s", UUID.randomUUID()));
            }

            ControllerMapper controllerMapper = new ControllerMapper();

            Controller controller = controllerMapper.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
