package webserver;

import db.DataBase;
import model.User;

import java.io.IOException;

public class CreateUserController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        saveUser(request);
        response.sendRedirect("/index.html");
    }

    private void saveUser(HttpRequest request) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // Todo: 400 BadRequest
        if (userId == null || password == null || name == null || email == null) {
            return;
        }
        DataBase.addUser(new User(userId, password, name, email));
    }
}
