package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

public class MercuryFreq {
	private double frequency;
	private double[] angle;
	
	public MercuryFreq(Mercury230Connection c) throws IOException, Mercury230ProtocolException 
	{
		c.sendParameterReadRequestPacket(0x16, 0x51);
		angle = c.read3dPacket();

		c.sendParameterReadRequestPacket(0x16, 0x40);
		byte[] packet = c.readNonRcPacket().getPayload();
		frequency = MercuryFixed.decode3b(packet,0);
	}

	public void dump()
	{
		System.out.println("Freq = "+frequency+" hz");
		System.out.println("Angle = "+angle[0]+" "+angle[1]+" "+angle[2]+" ");		
	}

	public double getFrequency() {
		return frequency;
	}

	public double[] getAngle() {
		return angle;
	}
	
}
