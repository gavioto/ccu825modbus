package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

public class MercuryActivePower extends MercuryRequest {
	protected double[] p;

	public MercuryActivePower(Mercury230Connection c) throws IOException, Mercury230ProtocolException
	{
		// Active power
		c.sendParameterReadRequestPacket(0x16, 0x00);
		p = c.read4dPacket();
	}

	/**
	 * 
	 * @return [0] is total, 1-3 - per phase
	 */
	public double[] getP() {
		return p;
	}

	public double getTotalP()
	{
		return p[0];
	}
	
	@Override
	public String toString() {
		return String.format("P = %6.2f %6.2f %6.2f Total=%6.2f (active)", p[1], p[2], p[3], p[0] );
	}
	
}
