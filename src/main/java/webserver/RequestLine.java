package webserver;

import exception.InvalidRequestLineException;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private String method;
    private String path;
    private Map<String, String> parameters = new HashMap<>();

    public RequestLine() {
    }

    public RequestLine(String method, String path, Map<String, String> parameters) {
        this.method = method;
        this.path = path;
        this.parameters = parameters;
    }

    public RequestLine(String requestString) {
        String[] words = requestString.split(" ");

        if (words.length != 3) {
            throw new InvalidRequestLineException();
        }

        method = getValidMethod(words[0]);

        if (method.equals("POST")) {
            path = words[1];
            return;
        }

        int index = words[1].indexOf("?");
        if (index == -1) {
            path = words[1];
            return;
        }

        path = words[1].substring(0, index);
        parameters = HttpRequestUtils.parseQueryString(words[1].substring(index + 1));
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    private String getValidMethod(String method) {
        if (method.equals("GET") || method.equals("POST")) {
            return method;
        }
        throw new InvalidRequestLineException();
    }
}
