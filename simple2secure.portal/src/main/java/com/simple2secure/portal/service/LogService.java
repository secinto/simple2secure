package com.simple2secure.portal.service;

import org.springframework.stereotype.Service;

import com.simple2secure.portal.model.LogInfo;
import com.simple2secure.portal.model.LogType;

@Service
public interface LogService {

	public void log(LogInfo logInfo);

	public void log(String format, Object... arguments);

	public void log(LogType type, String format, Object... arguments);

	public void log(String className, String format, Object... arguments);

	public void log(LogType type, String className, String format, Object... arguments);

	public void log(String user, String className, String format, Object... arguments);

	public void log(LogType type, String user, String className, String format, Object... arguments);

	public void log(String userAddress, String user, String className, String format, Object... arguments);

	public void log(LogType type, String userAddress, String user, String className, String format, Object... arguments);

	public void log(String action, String userAddress, String user, String className, String format, Object... arguments);

	public void log(LogType type, String action, String userAddress, String user, String className, String format, Object... arguments);

	public void log(String other, String action, String userAddress, String user, String className, String format, Object... arguments);

	public void log(LogType type, String other, String action, String userAddress, String user, String className, String format,
			Object... arguments);

	public LogInfo getInfo(String format, Object... arguments);

	public LogInfo getInfo(LogType type, String format, Object... arguments);

	public LogInfo getInfo(String className, String format, Object... arguments);

	public LogInfo getInfo(LogType type, String className, String format, Object... arguments);

	public LogInfo getInfo(String user, String className, String format, Object... arguments);

	public LogInfo getInfo(LogType type, String user, String className, String format, Object... arguments);

	public LogInfo getInfo(String userAddress, String user, String className, String format, Object... arguments);

	public LogInfo getInfo(LogType type, String userAddress, String user, String className, String format, Object... arguments);

	public LogInfo getInfo(String action, String userAddress, String user, String className, String format, Object... arguments);

	public LogInfo getInfo(LogType type, String action, String userAddress, String user, String className, String format,
			Object... arguments);

	public LogInfo getInfo(String other, String action, String userAddress, String user, String className, String format,
			Object... arguments);

	public LogInfo getInfo(LogType type, String other, String action, String userAddress, String user, String className, String format,
			Object... arguments);

}
