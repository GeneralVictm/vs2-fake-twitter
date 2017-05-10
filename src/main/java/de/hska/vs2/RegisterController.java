
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class RegisterController {

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void process(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final ITemplateEngine templateEngine) {
		
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		templateEngine.process("register", ctx, response.getWriter());
	}
	
}
