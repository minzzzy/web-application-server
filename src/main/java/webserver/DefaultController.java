package webserver;

import java.io.IOException;

public class DefaultController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getPath());
    }
}
