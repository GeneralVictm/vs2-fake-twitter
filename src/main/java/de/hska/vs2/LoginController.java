package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by sasch on 09.05.2017.
 */
@Controller
public class LoginController {
    @Autowired
    private RedisRepository repository;

    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    //TODO: ist nur C&P aus den Vorlesungsfolien, muss ggfs. noch an eigene Bedürfnisse angepasst werden
    //TODO: User-Objekt (+ Klasse) erstellen

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("user") @Valid User user, HttpServletResponse response, Model model) {
        if (repository.auth(user.getName(), user.getPass())) {
            String auth = repository.addAuth(user.getName(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            Cookie cookie = new Cookie("auth", auth);
            response.addCookie(cookie);
            model.addAttribute("user", user.getName());
            return "redirect:users/" + user.getName(); // wenn es nicht funktioniert: kopletter Pfad angeben
        }
        model.addAttribute("user", new User());
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
            final ServletContext servletContext, final ITemplateEngine templateEngine) {

		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		templateEngine.process("login", ctx, response.getWriter());
    }
}
