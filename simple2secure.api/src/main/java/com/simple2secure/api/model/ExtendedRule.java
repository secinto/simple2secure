package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class ExtendedRule extends GenericDBObject{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3105456893362818450L;
	
	private String name;
    private String expression;
    private String outcome;
    private int priority;
    private String namespace;
    private String description;
    
    public ExtendedRule() {
    }
    
    public ExtendedRule(final String name, final String expression, final String outcome, final int priority,
            final String namespace, final String description) {

        if(name == null) throw new AssertionError("name may not be null");
        if(expression == null) throw new AssertionError("expression may not be null");
        if(namespace == null) throw new AssertionError("namespace may not be null");
        
        this.name = name;
        this.expression = expression;
        this.outcome = outcome;
        this.priority = priority;
        this.namespace = namespace;
        this.description = description;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
