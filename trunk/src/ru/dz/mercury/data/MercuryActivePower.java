package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

public class MercuryActivePower {
	protected double[] p;

	public MercuryActivePower(Mercury230Connection c) throws IOException, Mercury230ProtocolException
	{
		// Active power
		c.sendParameterReadRequestPacket(0x16, 0x00);
		p = c.read4dPacket();
	}

	public double[] getP() {
		return p;
	}

	public void dump()
	{
		System.out.println("P = "+p[0]+" "+p[1]+" "+p[2]+" "+p[3]+" (active)");
	}

}
