package util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpCookieTest {
    @Test
    public void get_cookie() {
        String cookieName = "JSESSIONID";
        String cookieValue = "1234-5678";

        HttpCookie httpCookie = new HttpCookie(String.format("%s=%s;", cookieName, cookieValue));

        assertThat(httpCookie.getCookie(cookieName), is(cookieValue));
    }
}
