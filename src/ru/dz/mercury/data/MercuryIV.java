package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

/**
 * Voltage and current.
 * <p>
 * @author dz
 *
 */
public class MercuryIV {
	private double [] v;
	private double [] i;
	
	public MercuryIV(Mercury230Connection c)  throws IOException, Mercury230ProtocolException 
	{

		c.sendParameterReadRequestPacket(0x16, 0x11);
		v = c.read3dPacket();

		c.sendParameterReadRequestPacket(0x16, 0x21);
		i = c.read3dPacket();
	}

	public double[] getV() {
		return v;
	}

	public double[] getI() {
		return i;
	}

	/*
	 public double[] getVaverage() {

		return v;
	}
	 */
	public double getItotal() {
		return i[0]+i[1]+i[2];
	}

	public void dump() {
		System.out.println("V = "+v[0]+" "+v[1]+" "+v[2]+" ");
		System.out.println("I = "+i[0]+" "+i[1]+" "+i[2]+" ");		
	}
	
}
