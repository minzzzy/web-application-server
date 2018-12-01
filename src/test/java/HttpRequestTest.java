import exception.InvalidRequestLineException;
import org.junit.Test;
import util.HttpMethod;
import webserver.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources";

    @Test
    public void test_GET() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_GET.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_POST() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_POST.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_GET_with_params() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_GET_login_cookie.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
        assertEquals(true, httpRequest.isLogined());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_not_valid_method() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_not_valid_method.txt");
        HttpRequest httpRequest = new HttpRequest(in);
    }

    @Test(expected = InvalidRequestLineException.class)
    public void test_no_method() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_no_method.txt");
        HttpRequest httpRequest = new HttpRequest(in);
    }
}
