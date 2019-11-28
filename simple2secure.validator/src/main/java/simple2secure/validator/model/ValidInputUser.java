package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

import com.simple2secure.api.model.User;

public class ValidInputUser extends ValidatedInput<String>{
	
	private String userId;
	private String tag = "/{userId}";
	
	public ValidInputUser() {
	}
	
	public ValidInputUser(String userId) {
		this.userId = userId;
	}

	@Override
	public String getValue() {
		return userId;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		if (auth.isAuthenticated()) {
			if (auth.getPrincipal() != null) {
				User currentUser = (User) auth.getPrincipal();
				return new ValidInputUser(currentUser.getId());
			}
		}
		// TODO: Throw an exception that user is not authenticated
		return null;
	}
}
