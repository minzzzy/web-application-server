package util;

import webserver.*;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private Map<String, Controller> controllerMapper = new HashMap<>();
    private Controller defaultController = new DefaultController();

    public ControllerMapper() {
        controllerMapper.put("/user/create", new CreateUserController());
        controllerMapper.put("/user/login", new LoginController());
        controllerMapper.put("/user/list", new ListUserController());
    }

    public Controller getController(HttpRequest request) {
        return controllerMapper.getOrDefault(request.getPath(), defaultController);
    }
}
