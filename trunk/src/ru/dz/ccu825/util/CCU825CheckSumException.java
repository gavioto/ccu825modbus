package ru.dz.ccu825.util;

/**
 * Thrown if packet with a wrong checksum is received
 * @author dz
 *
 */

public class CCU825CheckSumException extends CCU825ProtocolException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6778582874107911185L;

	public CCU825CheckSumException() {
	}

	public CCU825CheckSumException(String message) {
		super(message);
	}

	public CCU825CheckSumException(Throwable cause) {
		super(cause);
	}

	public CCU825CheckSumException(String message, Throwable cause) {
		super(message, cause);
	}

	public CCU825CheckSumException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
