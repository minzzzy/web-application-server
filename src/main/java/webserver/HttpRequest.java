package webserver;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> parameters;
    private Map<String, String> headers;

    public HttpRequest() {
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public HttpRequest(InputStream in) {
        this();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String requestLine = bufferedReader.readLine();
            String[] requestLineWords = parseRequestLine(requestLine);
            String method = parseMethod(requestLineWords);
            if (method == null) {
                return;
            }

            String url = parseUrl(requestLineWords);
            if (url == null) {
                return;
            }

            this.headers = parseHeaders(bufferedReader);
            this.path = HttpRequestUtils.getPath(url);
            this.method = method;

            if (method.equals("GET")) {
                this.parameters = parseParams(HttpRequestUtils.getQueryString(url));
                return;
            }

            if (method.equals("POST")) {
                this.parameters = parseParams(IOUtils.readData(bufferedReader, getContentLength()));
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpRequest(FileInputStream in) {
        this();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String requestLine = bufferedReader.readLine();
            String[] requestLineWords = parseRequestLine(requestLine);
            String method = parseMethod(requestLineWords);
            if (method == null) {
                return;
            }

            String url = parseUrl(requestLineWords);
            if (url == null) {
                return;
            }

            this.headers = parseHeaders(bufferedReader);
            this.path = HttpRequestUtils.getPath(url);
            this.method = method;

            if (method.equals("GET")) {
                this.parameters = parseParams(HttpRequestUtils.getQueryString(url));
                return;
            }

            if (method.equals("POST")) {
                this.parameters = parseParams(IOUtils.readData(bufferedReader, getContentLength()));
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // Todo: null로 return 해주지 않는 방법은?
    private String parseMethod(String[] words) {
        String method = words[0].trim();
        if (method.equals("GET") || method.equals("POST")) {
            return method;
        }
        return null;
    }

    // Todo: null로 return 해주지 않는 방법은?
    private String parseUrl(String[] words) {
        String path = words[1].trim();
        if (!path.isEmpty()) {
            return path;
        }
        return null;
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(" ");
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
        }
        return headers;
    }

    private int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
