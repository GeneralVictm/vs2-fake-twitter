
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class RegisterController {
	
	private RedisRepository repository;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine) {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		templateEngine.process("register", ctx, response.getWriter());
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam("name") String name, @RequestParam("password") String password, @RequestParam("passw") String passw) {
		if (repository.isUserNameFree(name) && password == passw) {
			User user = new User(name, password);
			repository.addUser(user);
            String auth = repository.addAuth(user.getName(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            Cookie cookie = new Cookie("auth", auth);
            response.addCookie(cookie);
            model.addAttribute("user", user.getName());
            return "redirect:users/" + user.getName(); // wenn es nicht funktioniert: kopletter Pfad angeben
        }
        model.addAttribute("user", new User());
        return "redirect:login"; // gleiches wie oben
    }
}
