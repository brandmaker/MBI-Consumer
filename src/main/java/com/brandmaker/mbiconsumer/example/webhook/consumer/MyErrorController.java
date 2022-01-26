package com.brandmaker.mbiconsumer.example.webhook.consumer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brandmaker.mbiconsumer.example.dtos.Response;
import com.google.gson.Gson;

/**
 * Simple Error Controller just for beautiful error messages
 *
 * @author axel.amthor
 *
 */
@Controller
public class MyErrorController implements ErrorController {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MyErrorController.class);

	/**
	 * mapping for /error catches almost all http errors.
	 * Feel free to add particular handlers for certain error codes ...
	 * @param request - request object
	 * @param model - response mode
	 * @param ex - any thrown exception if there is one (!)
	 *
	 * @return - page name of a thymeleaf template w/o .html suffix
	 */
	@RequestMapping(
			value="/error",
			headers = "accept=text/*",
			produces="text/html"
	)
	public String handleTextError(HttpServletRequest request, Model model, Exception ex) {

		String cp = request.getContextPath();

		// generate some ui friendly messages
	    Object path =  cp + request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
	    Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE) +  (ex != null && ex.getMessage() != null ? " / " + ex.getMessage() : "");

	    // sanitize context path, the current config is "/spring/" with a trailing "/", you might want to change that
	    // put it all into request context for thymeleaf
	    model.addAttribute("t_root", cp.replaceAll("/$", "") + "/");
	    model.addAttribute("t_path", path);
	    model.addAttribute("t_status", status + " - " + HttpStatus.valueOf( status.intValue() ).getReasonPhrase());
	    model.addAttribute("t_message", message);

	    // we have an "error.html" in templates
   	    return "error";

	}
}
