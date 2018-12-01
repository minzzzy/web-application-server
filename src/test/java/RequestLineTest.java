import exception.InvalidRequestLineException;
import org.junit.Test;
import util.HttpMethod;
import webserver.RequestLine;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RequestLineTest {
    @Test
    public void parse_get_requestLine() {
        String requestString = "GET /user/create HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        assertEquals(requestLine.getMethod(), HttpMethod.GET);
        assertEquals(requestLine.getPath(), "/user/create");
    }

    @Test
    public void parse_post_requestLine() {
        String requestString = "POST /user/create HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        assertEquals(requestLine.getMethod(), HttpMethod.POST);
        assertEquals(requestLine.getPath(), "/user/create");
    }

    @Test
    public void parse_requestLine_with_params() {
        String requestString = "GET /user/create?userId=mingId&password=password HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        Map<String, String> parameters = requestLine.getParameters();
        assertEquals(requestLine.getMethod(), HttpMethod.GET);
        assertEquals(requestLine.getPath(), "/user/create");
        assertEquals(parameters.get("userId"), "mingId");
        assertEquals(parameters.get("password"), "password");
    }

    @Test(expected = InvalidRequestLineException.class)
    public void invalid_request_string() {
        String invalidRequestString = "GET /user/create";
        new RequestLine(invalidRequestString);
    }

}
