package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class DefaultController extends AbstractController {
    private static final String PATH = "/";

    @Override
    public String getPath() {
        return PATH;
    }
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getPath(), request.getHeader("Accept"));
    }
}
