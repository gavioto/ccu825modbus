package ru.dz.ccu825.push;

import java.util.logging.Logger;

import ru.dz.ccu825.CCU825Connection;
import ru.dz.ccu825.util.CCU825ProtocolException;

class OutBitPollItemHandler extends PollItemHandler
{
	private final static Logger log = Logger.getLogger(OutBitPollItemHandler.class.getName());

	private final int nOutBit;
	private CCU825Connection conn;

	/**
	 * 
	 * @param nOutBit CCU825 output number (bit pos)
	 */
	public OutBitPollItemHandler(int nOutBit, CCU825Connection conn) {
		this.nOutBit = nOutBit;
		this.conn = conn;
	}
	
	@Override
	public void transfer(String item, String value) {
		try {
			conn.setOutState(nOutBit, value.equalsIgnoreCase("ON"));
		} catch (CCU825ProtocolException e) {
			log.severe(e.getMessage());
			//e.printStackTrace();
		}		
	}
}