package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputDevice extends ValidatedInput<String>{
	
	private String deviceId;
	private String tag = "/{deviceId}";
	
	public ValidInputDevice() {
	}
	
	public ValidInputDevice(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String getValue() {
		return deviceId;
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
}
