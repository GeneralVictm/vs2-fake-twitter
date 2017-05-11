package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sascha on 09.05.2017.
 *
 * Controller to handle Login, Logout and active Sessions
 */
@Controller
public class LoginController {
    @Autowired
    private RedisRepository repository;

    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    //TODO: ist nur C&P aus den Vorlesungsfolien, muss ggfs. noch an eigene Bedürfnisse angepasst werden
    //TODO: User-Objekt (+ Klasse) erstellen

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletResponse response, WebRequest request) {
        //TODO Testen ob Parameter-Namen korrekt sind
        String name = request.getParameter("name");
        String pass = request.getParameter("password");
        if (repository.auth(name, pass)) {
            String auth = repository.addAuth(name, TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            Cookie cookie = new Cookie("auth", auth);
            response.addCookie(cookie);
            return "redirect:users/" + name; // wenn es nicht funktioniert: kompletter Pfad angeben
        }
        return "redirect:login"; // gleiches wie oben
    }

    @RequestMapping(value = "/blog/logout", method = RequestMethod.GET)
    public String logout() {
        if (SecurityInfo.isSignedIn()) {
            String name = SecurityInfo.getName();
            repository.deleteAuth(name);
        }
        return "redirect:/";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void process(final HttpServletRequest request, final HttpServletResponse response,
                        final ServletContext servletContext, final ITemplateEngine templateEngine) throws Exception {

		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		templateEngine.process("login", ctx, response.getWriter());
    }
    
    
}
