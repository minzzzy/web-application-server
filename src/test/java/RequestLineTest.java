import exception.InvalidRequestLineException;
import org.junit.Test;
import webserver.RequestLine;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RequestLineTest {
    @Test
    public void parse_get_requestLine() {
        String requestString = "GET /user/create HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        assertThat(requestLine.getMethod(), is("GET"));
        assertThat(requestLine.getPath(), is("/user/create"));
    }

    @Test
    public void parse_post_requestLine() {
        String requestString = "POST /user/create HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        assertThat(requestLine.getMethod(), is("POST"));
        assertThat(requestLine.getPath(), is("/user/create"));
    }

    @Test
    public void parse_requestLine_with_params() {
        String requestString = "GET /user/create?userId=mingId&password=password HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestString);

        Map<String, String> parameters = requestLine.getParameters();
        assertThat(requestLine.getMethod(), is("GET"));
        assertThat(requestLine.getPath(), is("/user/create"));
        assertThat(parameters.get("userId"), is("mingId"));
        assertThat(parameters.get("password"), is("password"));
    }

    @Test(expected = InvalidRequestLineException.class)
    public void invalid_request_string() {
        String invalidRequestString = "GET /user/create";
        new RequestLine(invalidRequestString);
    }

}
