package ru.dz.ccu825.util;

/**
 * General protocol exception
 * @author dz
 *
 */


public class CCU825ProtocolException extends CCU825Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2660898806731667173L;

	public CCU825ProtocolException() {
	}

	public CCU825ProtocolException(String message) {
		super(message);
	}

	public CCU825ProtocolException(Throwable cause) {
		super(cause);
	}

	public CCU825ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public CCU825ProtocolException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
