package ru.dz.ccu825.transport;

/**
 * Empty connector to test code with no comm at all.
 * @author dz
 *
 */

public class EmptyModbusConnector implements IModBusConnection {

	@Override
	public void setSpeed(int baud) {
	}

	@Override
	public void connect() {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public byte[] rwMultiple(int nRead, byte[] writeData) {
		return new byte[0];
	}

}
