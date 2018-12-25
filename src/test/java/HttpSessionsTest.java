import org.junit.Before;
import org.junit.Test;
import webserver.HttpSession;
import webserver.HttpSessions;

import static org.junit.Assert.assertEquals;

public class HttpSessionsTest {

    private String id;
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        id = "1234-5678a";
        session = HttpSessions.getSession(id);
    }

    @Test
    public void create_httpSession() {
        assertEquals(id, session.getId());
    }

    @Test
    public void remove_httpSession() {
        HttpSessions.remove(id);

        assertEquals(null, HttpSessions.getSessions().get(id));
    }
}

