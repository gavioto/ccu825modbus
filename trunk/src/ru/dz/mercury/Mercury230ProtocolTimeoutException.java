package ru.dz.mercury;

public class Mercury230ProtocolTimeoutException extends
		Mercury230ProtocolException {

	public Mercury230ProtocolTimeoutException() {
		super("Packet reception timeout");
	}

	public Mercury230ProtocolTimeoutException(String message) {
		super(message);
	}

	public Mercury230ProtocolTimeoutException(Throwable cause) {
		super("Packet reception timeout",cause);
	}

	public Mercury230ProtocolTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public Mercury230ProtocolTimeoutException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
