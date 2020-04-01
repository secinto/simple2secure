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
@S2SDSL(value="sdc.sut")
public class SDCSystemUnderTest extends SystemUnderTest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -678065385609930382L;
	
	private @NonNull String port;
}
