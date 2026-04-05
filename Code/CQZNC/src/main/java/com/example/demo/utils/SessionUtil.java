package com.example.demo.utils;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {}

    public static User currentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    public static boolean hasRole(HttpSession session, UserRole role) {
        User u = currentUser(session);
        return u != null && u.getRole() == role;
    }
}
