import org.junit.Test;
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

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_POST() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory) + "/Http_POST.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }
}
