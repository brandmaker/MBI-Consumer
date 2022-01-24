package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.google.gson.Gson;

@Component
@ControllerAdvice
//@Order(-1)
public class RestResponseEntityExceptionHandler extends DefaultHandlerExceptionResolver {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);
	
	
	@ExceptionHandler(value= HttpMessageNotReadableException.class)
    @Override
    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
        
		LOGGER.info("Error 1: " + ex.getMessage() + " - " + ex.getClass().getName() );
		
		Response r = new Response("Error 1: " + ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		
		sendJsonErrorMessage(response, r);   
        
        return new ModelAndView();
    }
	
	@ExceptionHandler(value= MissingServletRequestParameterException.class)
	@Override
	protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {	
		
		LOGGER.info("Error 3: " + ex.getMessage() + " - " + ex.getClass().getName() + ": " + request.getContentType());
		
		Response r = new Response("Error 3: " + ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		
		sendJsonErrorMessage(response, r);   
        return new ModelAndView();
        
	}
	
	@ExceptionHandler(value= HttpMediaTypeNotAcceptableException.class)
	@Override
	protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
		
		LOGGER.info("Error 2: " + ex.getMessage() + " - " + ex.getClass().getName() + ": " + request.getContentType());
		
		Response r = new Response("Error 2: " + ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		
		sendJsonErrorMessage(response, r);   
        return new ModelAndView();
        
	}
	
	private void sendJsonErrorMessage(HttpServletResponse response, Response r) throws IOException {
		PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(r.getCode());
        out.print((new Gson()).toJson(r));
        out.flush();
	}
}
