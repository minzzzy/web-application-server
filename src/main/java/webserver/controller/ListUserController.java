package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class ListUserController extends AbstractController {
    private static final String PATH = "/user/list";

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (!request.isLogined()) {
            response.sendRedirect("/user/login.html");
            return;
        }
        response.forward(request.getPath(), request.getHeader("Accept"));
    }
}
