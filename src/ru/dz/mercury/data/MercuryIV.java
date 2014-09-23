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
public class MercuryIV extends MercuryRequest {
	private double [] v;
	private double [] i;
	
	public MercuryIV(Mercury230Connection c)  throws IOException, Mercury230ProtocolException 
	{

		c.sendParameterReadRequestPacket(0x16, 0x11);
		v = c.read3dPacket();

		c.sendParameterReadRequestPacket(0x16, 0x21);
		i = MercuryFixed.multiply(0.1, c.read3dPacket());
	}

	public double[] getV() {
		return v;
	}

	public double[] getI() {
		return i;
	}

	
	 public double getVaverage() {
		return (v[0]+v[1]+v[2])/3;
	}
	 
	public double getItotal() {
		return i[0]+i[1]+i[2];
	}


	@Override
	public String toString() {
		return String.format("V = %6.2f %6.2f %6.2f%nI = %6.2f %6.2f %6.2f", v[0], v[1], v[2], i[0], i[1], i[2]);		
	}
	
}
