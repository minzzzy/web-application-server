package util;

import model.User;
import org.junit.Before;
import org.junit.Test;
import webserver.HttpSession;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SessionUtilsTest {

    private HttpSession httpSession;

    @Before
    public void setUp() throws Exception {
        httpSession = new HttpSession("1234-5678");
    }

    @Test
    public void is_logined() {
        httpSession.setAttribute("user", new User("userId", "pwd", "name", "email"));

        assertThat(SessionUtils.isLogined(httpSession), is(true));
    }

    @Test
    public void is_not_logined() {
        assertThat(SessionUtils.isLogined(httpSession), is(false));
    }

}
