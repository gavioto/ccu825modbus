package ru.dz.ccu825;

import java.util.logging.Logger;

import ru.dz.ccu825.data.CCU825ReturnCode;
import ru.dz.ccu825.util.CCU825Exception;

public abstract class AbstractRequestLoop {
	private final static Logger log = Logger.getLogger(AbstractRequestLoop.class.getName());

	private CCU825Connection c;
	private boolean doPoll = true;

	public AbstractRequestLoop(CCU825Connection c) {
		this.c = c;
	}

	/**
	 * Connect and start polling. 
	 */
	public void startBlocking() {
		connectLoop();
	}

	/**
	 * Request polling loop to exit.
	 */
	public void stop() {
		doPoll = false;
	}

	/**
	 * Is stop requested.
	 * <p>
	 * Implemented in subclass pollDevice() must check this and exit if true.
	 * 
	 * @return True if we are requested to stop by stop() method.
	 * 
	 */
	protected boolean isStopped() {
		return !doPoll;
	}

	private void connectLoop()
	{
		while(doPoll)
		{
			try {
				connectAndPoll();
			} catch (CCU825Exception e) {
				log.severe(e.getMessage());
			}

			// Reconnect on exception

			c.disconnect();
		}

	}

	private void connectAndPoll() throws CCU825Exception 
	{
		if(!c.isConnected())
		{
			Say( "Connecting via "+c.getModbusConnector().getDestination() );

			CCU825ReturnCode protocolRC = c.connect();

			Say("Connected, RC = " + protocolRC );

			if(!protocolRC.isOk())
			{
				log.severe("Bad connect return code "+protocolRC);
				System.exit(33);
			}
		}
		Say( c.getDeviceInfo().toString() );

		//System.out.println( c.getSysInfo() );

		while(doPoll)
			pollDevice(c);
	}

	protected abstract void Say(String string);

	/**
	 * Must be implemented in subclass to actually do some data transfer.
	 * <p>
	 * Called in loop forever (or as long as stop() is not called).
	 * 
	 * @param c Device connection in connected state.
	 * @throws CCU825Exception 
	 */
	protected abstract void pollDevice(CCU825Connection c) throws CCU825Exception;

}
