package com.simple2secure.portal.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class PasswordValidator implements Validator{

    @Autowired
    MessageByLocaleService messageByLocaleService;	
	
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}
	
	private Pattern pattern;
	private Matcher matcher;
	private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,50}$";
	
	public PasswordValidator() {
		pattern = Pattern.compile(passwordRegex);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;
		
		String password = user.getPassword();
		
		ValidationUtils.rejectIfEmpty(errors, "password", "password", messageByLocaleService.getMessage("password_cannot_be_null"));
		
        if (!Strings.isNullOrEmpty(password)) {

            if (password.length() < 8) {
                errors.rejectValue("password", "password", messageByLocaleService.getMessage("password_should_contain_8_chars"));
            }

            if (password.length() > 50) {
                errors.rejectValue("password", "password", messageByLocaleService.getMessage("passowrd_max_length_reached"));
            }
        }
        
        if (!errors.hasErrors()) {
            matcher = pattern.matcher(password);
            if (!matcher.matches()) {
                errors.rejectValue("password", "password", messageByLocaleService.getMessage("password_rules_not_satisfied"));
            }
        }		
	}

}
