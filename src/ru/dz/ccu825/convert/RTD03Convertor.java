package ru.dz.ccu825.convert;

public class RTD03Convertor implements IConvertor 
{
	/*
	 * 0-10v
	 * v = (0.5 + 0.01 * t) * 5 
	 */

	// TODO corrections
	private double tOffset = 0;
	private double tMult = 1;
	
	/**
	 * Voltage to temperature
	 */
	@Override
	public double convert(double in) 
	{
		return ((in/5) - 0.5) * 100;
	}

	/**
	 * Temperature to voltage
	 */
	@Override
	public double convertBack(double in) 
	{
		return (0.5 + 0.01 * in) * 5;
	}

}
