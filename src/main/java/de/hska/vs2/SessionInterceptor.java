package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sasch on 10.05.2017.
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private StringRedisTemplate template;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (!ObjectUtils.isEmpty(cookies)) for (Cookie cookie : cookies)
            if (cookie.getName().equals("auth")) {
                String auth = cookie.getValue();
                if (auth != null) {
                    String uid = template.opsForValue().get("auth:" + auth + ":uid");
                    if (uid != null) {
                        String name = (String) template.boundHashOps("uid:" + uid + ":user").get("name");
                        SecurityInfo.setUser(name, uid);
                    }
                }
            }
        return true;
    }

    // ggfs. SecurityInfo in afterCompletion zurücksetzen? (user = null)
}
