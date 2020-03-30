package com.simple2secure.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@S2SDSL(value="ldc.sut")
public class LDCSystemUnderTest extends SystemUnderTest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4994023419133198440L;
	
	private @NonNull String ipAddress;
	private @NonNull String port;
	private @NonNull Protocol protocol;

}
