import org.junit.Test;
import webserver.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources";

    @Test
    public void test_GET() throws FileNotFoundException {
        FileInputStream in = new FileInputStream(new File(testDirectory) + "/Http_GET.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }

    @Test
    public void test_POST() throws FileNotFoundException {
        FileInputStream in = new FileInputStream(new File(testDirectory) + "/Http_POST.txt");
        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("mingId", httpRequest.getParameter("userId"));
    }
}
