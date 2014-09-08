package ru.dz.ccu825.payload;

/**
 * CCU825 protocol return codes.
 * 
 * @author dz
 *
 */

public class CCU825ReturnCode {

	private static final int CODE_OK = 6;
	
	private int code;
	
	public CCU825ReturnCode(int rc) {
		code = rc;
	}

	public int getCode() {
		return code;
	}
	
	static String codeNames[] = {
		"Unknown packet",
		"Unknown command",
		"Command format error",
		"Unknown ack code",
		"Out of memory",
		"Not authentified",
		"OK",
		"Flash memory error",
		"Wrong admion password",		
	};
	
	public boolean isOk() { return code == CODE_OK; }
	
	@Override
	public String toString() 
	{
		if( (code < 0) || (code >= codeNames.length) )
			return "(undefined return code)";
		
		return codeNames[code];
	}
	
}
