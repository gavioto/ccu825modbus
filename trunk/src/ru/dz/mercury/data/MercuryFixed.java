package ru.dz.mercury.data;

/**
 * 3- or 4-byte fixed point number.
 * @author dz
 *
 */
public class MercuryFixed {

	/**
	 * Decode Mercury's 3-byte fixed number as 3-byte int/100
	 * <p>
	 * 
	 * 
	 * @param packet where to get data from
	 * @param pos start byte position
	 * @return decoded double value
	 */
	public static double decode3b(byte[] packet, int pos) {
		int i;
		
		i = ((int)packet[pos+0]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+2]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+1]) & 0xFF;
		
		return i/100.0;
	}
	
	/**
	 * Decode Mercury's 3-byte fixed number as 3-byte int/100
	 * <p>
	 * 
	 * 
	 * @param packet where to get data from
	 * @return decoded double value
	 */
	public static double decode3b(byte[] packet) {
		return decode3b(packet, 0);		
	}

	
	/**
	 * Decode Mercury's 4-byte fixed number as 4-byte int/1000
	 * <p>
	 * 
	 * 
	 * @param packet where to get data from
	 * @param pos start byte position
	 * @return decoded double value
	 */
	public static double decode4b(byte[] packet, int pos) {
		int i;
		
		i = ((int)packet[pos+1]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+0]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+3]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+2]) & 0xFF;
		
		return i/1000.0;
	}
	
	/**
	 * Decode Mercury's 4-byte fixed number as 4-byte int/1000
	 * <p>
	 * 
	 * 
	 * @param packet where to get data from
	 * @return decoded double value
	 */
	public static double decode4b(byte[] packet) {
		return decode3b(packet, 0);		
	}

	
	// ---------------------------------------------------------------------------
	// Decode blocks of fixed numbers
	// ---------------------------------------------------------------------------
	
	/**
	 * Typical V/I/etc reply is 3 values 3 byte each
	 * <p>
	 * 
	 * @param packet Packet to decode
	 * @param v array of 3 values to put result to
	 */
	public static void decode3x3(byte[] packet, double[] v) {
		v[0] = decode3b(packet,0);
		v[1] = decode3b(packet,3);
		v[2] = decode3b(packet,6);
	}
	
	/**
	 * Typical energy meter reply is 4 values 4 bytes each
	 * <p>
	 * 
	 * @param packet Packet to decode
	 * @param v array of 4 values to put result to
	 */
	public static void decode4x4(byte[] packet, int startPos, double[] v) {
		v[0] = decode4b(packet,startPos+0);
		v[1] = decode4b(packet,startPos+4);
		v[2] = decode4b(packet,startPos+8);
		v[3] = decode4b(packet,startPos+12);
	}

	public static double[] multiply(double d, double[] vector) {
		
		int len = vector.length;
		double [] ret = new double[len];
		
		for( int i = 0; i < len; i++ )
			ret[i] = vector[i] * d;
		
		return ret;
	}
	
}
