package com.simple2secure.portal.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.data.domain.Pageable;

public class PageableValidator implements ConstraintValidator<PageableConstraint, Pageable> {

	private int maxPerPage;

	@Override
	public void initialize(PageableConstraint constraintAnnotation) {
		maxPerPage = constraintAnnotation.maxPerPage();
	}

	@Override
	public boolean isValid(Pageable value, ConstraintValidatorContext context) {
		return value.getPageSize() <= maxPerPage;
	}
}
