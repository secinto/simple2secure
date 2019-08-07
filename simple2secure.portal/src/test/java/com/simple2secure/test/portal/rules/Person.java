package com.simple2secure.test.portal.rules;

import org.joda.time.DateTime;

public class Person 
{
	private DateTime birthDate;
	private String name;
	private String address;
	
	public Person(DateTime birthDate, String name, String address) 
	{
		this.birthDate = birthDate;
		this.name = name;
		this.address = address;
	}

	public DateTime getBirthDate() {
		return birthDate;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
}


