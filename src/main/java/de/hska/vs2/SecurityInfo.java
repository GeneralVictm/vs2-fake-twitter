package de.hska.vs2;

import org.springframework.core.NamedThreadLocal;

/**
 * Created by Sascha on 10.05.2017.
 *
 * Thread-wise Session store
 */
public abstract class SecurityInfo {
    private static final ThreadLocal<UserInfo> user = new NamedThreadLocal<UserInfo>("microblog-id");

    private static class UserInfo {
        String name;
        String uid;
    }

    public static void setUser(String name, String uid) {
        UserInfo userInfo = new UserInfo();
        userInfo.name = name;
        userInfo.uid = uid;
        user.set(userInfo);
    }

    public static boolean isUserSignedIn(String name) {
        UserInfo userInfo = user.get();
        return userInfo != null && userInfo.name.equals(name);
    }

    public static boolean isSignedIn() {
        UserInfo userInfo = user.get();
        return userInfo != null;
    }

    public static String getName() {
        UserInfo userInfo = user.get();
        if (userInfo != null) {
            return userInfo.name;
        }

        return "";
    }

    public static String getUid() {
        UserInfo userInfo = user.get();
        if (userInfo != null) {
            return userInfo.uid;
        }

        return "";
    }
}