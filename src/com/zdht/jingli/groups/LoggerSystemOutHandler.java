package com.zdht.jingli.groups;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerSystemOutHandler extends Handler {

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		System.out.println("--->"+record.getMessage());
	}

}
