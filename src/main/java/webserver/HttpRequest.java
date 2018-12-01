package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpMethod;
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
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest() {
    }

    public HttpRequest(InputStream in) throws IOException {
        this();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String requestLineString = bufferedReader.readLine();
        if (requestLineString == null) {
            return;
        }

        requestLine = new RequestLine(requestLineString);
        headers = parseHeaders(bufferedReader);

        if (requestLine.getMethod().isPost()) {
            parameters = parseParams(IOUtils.readData(bufferedReader, getContentLength()));
            return;
        }

        parameters = requestLine.getParameters();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean isLogined() {
        String cookieValue = headers.get("Cookie");
        if (cookieValue == null) {
            return false;
        }

        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        return Boolean.parseBoolean(cookies.get("logined"));
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
