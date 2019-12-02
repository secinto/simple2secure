package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputSize extends ValidatedInput<Integer>{
	
	private int size;
	private String tag = "/{size}";
	
	public ValidInputSize() {
	}
	
	public ValidInputSize(int size) {
		this.size = size;
	}

	@Override
	public Integer getValue() {
		return size;
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
	public Object validatePathVariable(Integer value) {
		if(value > 20) {
			value = 20;
		}
		return new ValidInputSize(value);
	}
}


