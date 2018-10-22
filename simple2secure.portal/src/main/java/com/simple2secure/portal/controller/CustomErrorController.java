package com.simple2secure.portal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
public class CustomErrorController implements ErrorController {
	private static final String PATH = "/error";
	
    @Autowired
    private ErrorAttributes errorAttributes;

	@RequestMapping(value = PATH)
    public Map<String, Object> error(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errAttributes = getErrorAttributes(request, false); 
        map.put("status", response.getStatus());
        map.put("reason", errAttributes);
        map.put("errorMessage", getErrorMessage(errAttributes));
        return map;
    }
	
    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
    
    private String getErrorMessage(Map<String, Object> attributes) {
    	return (String) attributes.get("message");
    }

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
