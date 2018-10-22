package com.simple2secure.api.model;

import java.io.ByteArrayOutputStream;

public class CommandExecutionResult {
	
    private ByteArrayOutputStream output;
    private ByteArrayOutputStream error;
    
    public String getOutput() {
        return output.toString();
    }

    public void setOutput(ByteArrayOutputStream output) {
        this.output = output;
    }

    public String getError() {
        return error.toString();
    }

    public void setError(ByteArrayOutputStream error) {
        this.error = error;
    }
}
