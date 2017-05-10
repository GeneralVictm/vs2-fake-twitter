package de.hska.vs2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Controller
public class RegisterController {
	@Autowired
	private RedisRepository repository;

    private static final Duration TIMEOUT = Duration.ofMinutes(15);

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
                        final ServletContext servletContext, final ITemplateEngine templateEngine) {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		templateEngine.process("register", ctx, response.getWriter());
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam("name") String name, @RequestParam("password") String password, @RequestParam("passw") String passw, HttpServletResponse response, Model model) {
		if (repository.isUsernameFree(name) && password.equals(passw)) {
			User user = new User(name, password);
			repository.addUser(user);
            String auth = repository.addAuth(user.getName(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            Cookie cookie = new Cookie("auth", auth);
            response.addCookie(cookie);
            model.addAttribute("user", user.getName());
            return "redirect:users/" + user.getName(); // wenn es nicht funktioniert: kopletter Pfad angeben
        }
        model.addAttribute("user", new User()); // User-Konstruktor ohne Parameter nötig oder geht das auch schöner?
        return "redirect:login"; // gleiches wie oben
    }
}
