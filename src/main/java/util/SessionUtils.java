package util;

import webserver.HttpSession;

public class SessionUtils {
    public static boolean isLogined(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return true;
    }
}

