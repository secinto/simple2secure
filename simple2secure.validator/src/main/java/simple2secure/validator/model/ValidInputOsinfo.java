package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputOsinfo extends ValidatedInput<String>{
	
	private String osinfo;
	private String tag = "/{osinfo}";
	
	public ValidInputOsinfo() {
	}
	
	public ValidInputOsinfo(String osinfo) {
		this.osinfo = osinfo;
	}

	@Override
	public String getValue() {
		return osinfo;
	}
	
	public void setValue(String value) {
		this.osinfo = value;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Object validate(Authentication auth, MethodParameter methodParameter, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object validatePathVariable(String value) {
		return new ValidInputOsinfo(value);
	}
}
