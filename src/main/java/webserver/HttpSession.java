package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private String sessionId;
    private Map<String, Object> attributes;

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return sessionId;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(sessionId);
    }
}
