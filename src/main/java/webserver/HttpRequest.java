package webserver;

import exception.InvalidRequestLineException;
import exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private String method;
    private String path;
    private Map<String, String> parameters;
    private Map<String, String> headers;

    public HttpRequest() {
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public HttpRequest(InputStream in) throws IOException {
        this();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return;
        }

        processRequestLine(requestLine);

        this.headers = parseHeaders(bufferedReader);

        if (method.equals("POST")) {
            this.parameters = parseParams(IOUtils.readData(bufferedReader, getContentLength()));
            return;
        }
    }

    private void processRequestLine(String requestLine) {
        String[] words = parseRequestLine(requestLine);
        method = parseMethod(words);
        path = parseUrlToPath(words);

        if (method.equals("POST")) {
            return;
        }

        String queryString = HttpRequestUtils.getQueryString(words[1]);

        if (queryString == null) {
            return;
        }

        this.parameters = parseParams(queryString);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    private String parseMethod(String[] words) {
        String method = words[0].trim();
        if (!method.equals("GET") && !method.equals("POST")) {
            throw new NotFoundException();
        }
        return method;
    }

    private String parseUrlToPath(String[] words) {
        String url = words[1].trim();
        if (url.isEmpty()) {
            throw new NotFoundException();
        }
        return HttpRequestUtils.getPath(url);
    }

    private String[] parseRequestLine(String requestLine) {
        log.debug("requestLine: {}", requestLine);
        String[] words = requestLine.split(" ");
        if (words.length != 3) {
            throw new InvalidRequestLineException();
        }
        return words;
    }

    private Map<String, String> parseParams(String queryString) {
        return HttpRequestUtils.parseQueryString(queryString);
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        HashMap<String, String> headers = new HashMap<>();
        while (!"".equals(line)) {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
            line = bufferedReader.readLine();
            log.debug("line: {}", line);
        }
        return headers;
    }

    private int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
