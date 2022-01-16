package com.brandmaker.mbiconsumer.example.webhook.consumer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple Error Controller just for beautiful error messages 
 * 
 * @author axel.amthor
 *
 */
@Controller
public class MyErrorController implements ErrorController {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MyErrorController.class);
	
	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model, Exception ex) {

		LOGGER.info("Exception: " + (ex != null ? ex.getMessage() : "none") );
		
	    Object path = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
	    Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    
	    Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE) +  (ex != null && ex.getMessage() != null ? " / " + ex.getMessage() : "");

	    model.addAttribute("t_path", path);
	    model.addAttribute("t_status", status + " - " + HttpStatus.valueOf( status.intValue() ).getReasonPhrase());
	    model.addAttribute("t_message", message);
	    
	    // we have an "error.html" in templates
	    return "error";
	
	}
}
