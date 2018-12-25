package webserver.controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.HttpSession;

import java.io.IOException;

public class LoginController extends AbstractController {
    private static final String PATH = "/user/login";

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (!login(request)) {
            response.sendRedirect("/user/login_failed.html");
            return;
        }
        response.sendRedirect("/index.html");
    }

    public boolean login(HttpRequest request) {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user == null) {
            return false;
        }
        if (!request.getParameter("password").equals(user.getPassword())) {
            return false;
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return true;
    }
}
