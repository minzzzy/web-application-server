package util;

import webserver.HttpRequest;
import webserver.controller.*;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private Map<String, Controller> controllerMapper = new HashMap<>();
    private Controller defaultController = new DefaultController();

    public ControllerMapper() {
        putPathToMapper(new CreateUserController());
        putPathToMapper(new LoginController());
        putPathToMapper(new ListUserController());
    }

    public Controller getController(HttpRequest request) {
        return controllerMapper.getOrDefault(request.getPath(), defaultController);
    }

    private void putPathToMapper(Controller controller) {
        controllerMapper.put(controller.getPath(), controller);
    }
}
