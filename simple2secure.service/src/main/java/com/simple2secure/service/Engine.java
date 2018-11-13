package com.simple2secure.service;

public interface Engine {

	public boolean isStopped();

	public boolean start();

	public boolean stop();

	public String getName();
}
