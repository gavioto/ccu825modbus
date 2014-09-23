package ru.dz.mercury;

public class Mercury230CRCException extends Mercury230ProtocolException {

	private byte[] pkt;

	public Mercury230CRCException() {
		// TODO Auto-generated constructor stub
	}

	public Mercury230CRCException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public Mercury230CRCException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public Mercury230CRCException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public Mercury230CRCException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public Mercury230CRCException(byte[] pkt) {
		this.pkt = pkt;
	}

}
