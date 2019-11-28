package simple2secure.validator.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;

public class ValidInputReport extends ValidatedInput<String>{
	
	private String reportId;
	private String tag = "/{reportId}";
	
	public ValidInputReport() {
	}
	
	public ValidInputReport(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String getValue() {
		return reportId;
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
