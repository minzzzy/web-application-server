package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public HttpSessions() {
    }

    public static Map<String, HttpSession> getSessions() {
        return sessions;
    }

    public static HttpSession getSession(String sessionId) {
        HttpSession httpSession = sessions.get(sessionId);

        if (httpSession == null) {
            HttpSession createdSession = new HttpSession(sessionId);
            sessions.put(sessionId, createdSession);
            return createdSession;
        }

        return httpSession;
    }

    public static void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
