package webserver;

import db.DataBase;
import model.User;

import java.io.IOException;

public class LoginController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        boolean isLogined = login(request);
        if (!isLogined) {
            response.sendRedirect("/user/login_failed.html");
            return;
        }
        response.addHeader("Set-Cookie", "logined=true");
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
        return true;
    }
}
