package ru.dz.ccu825.convert;

/**
 * Convert RTD03 output voltage to temperature.
 * <p>
 * Can be tuned with offset and multiplier.
 * 
 * @author dz
 *
 */
public class RTD03Convertor implements IConvertor 
{
	/*
	 * 0-10v
	 * v = (0.5 + 0.01 * t) * 5 
	 */


	private double tOffset = 0;
	private double tMult = 1;
	
	/**
	 * Voltage to temperature
	 * 
	 */
	@Override
	public double convert(double in) 
	{
		return (((in/5) - 0.5) * 100 * tMult) + tOffset;
	}

	/**
	 * Temperature to voltage
	 * 
	 */
	@Override
	public double convertBack(double in) 
	{
		return (0.5 + 0.01 * ((in-tOffset)/tMult) ) * 5;
	}


	/**
	 * Temperature offset
	 * @param tOffset When converting v to t, add this offset to result. After tMult!
	 */
	public void settOffset(double tOffset) {		this.tOffset = tOffset;	}
	public double gettOffset() {		return tOffset;	}


	/**
	 * Temperature coefficient
	 * @param tMult When converting v to t, multiply result to this. Before tOffset!
	 */
	public void settMult(double tMult) {		this.tMult = tMult;	}
	public double gettMult() {		return tMult;	}

}
