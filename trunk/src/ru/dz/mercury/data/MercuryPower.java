package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

public class MercuryPower extends MercuryActivePower 
{
	private double[] q;
	private double[] s;
	
	public MercuryPower(Mercury230Connection c) throws IOException, Mercury230ProtocolException 
	{
		super(c);
		
		// Reactive power
		c.sendParameterReadRequestPacket(0x16, 0x04);
		q = c.read4dPacket();

		// Full (P+Q) power
		c.sendParameterReadRequestPacket(0x16, 0x08);
		s = c.read4dPacket();
	}
	
	public void dump()
	{
		super.dump();
		System.out.println("Q = "+q[0]+" "+q[1]+" "+q[2]+" "+q[3]+" (reactive)");
		System.out.println("S = "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]+" (full)");
	}

	public double[] getQ() {
		return q;
	}

	public double[] getS() {
		return s;
	}
	
}
