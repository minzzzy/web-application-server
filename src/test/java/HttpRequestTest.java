import exception.InvalidRequestLineException;
import org.junit.Test;
import util.HttpMethod;
import webserver.HttpRequest;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources";

    @Test
    public void test_GET() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_GET.txt"));

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_POST() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_POST.txt"));

        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_GET_with_params() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_GET_login_cookie.txt"));

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_GET_with_cookie() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_GET_login_cookie.txt"));

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("1234-5678", httpRequest.getCookies().getCookie("JSESSIONID"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_not_valid_method() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_not_valid_method.txt"));
    }

    @Test(expected = InvalidRequestLineException.class)
    public void test_no_method() throws IOException {
        HttpRequest httpRequest = new HttpRequest(getIntputStreamFromFile("/Http_no_method.txt"));
    }

    private InputStream getIntputStreamFromFile(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(testDirectory) + fileName);
    }
}
