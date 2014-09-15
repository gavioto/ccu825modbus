package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;

public abstract class AbstractSysInfo implements ICCU825SysInfo
{

	protected  int inBits;
	protected  int outBits;
	protected  double[] inValue;
	protected  boolean powerOk;
	protected  boolean balanceValid;
	protected  boolean caseOpen;
	protected  byte batteryPercentage;
	protected  byte deviceTemperature;
	protected  double powerVoltage;
	protected  double GSMBalance;

	AbstractSysInfo() {
		super();
	}

	abstract void decodePayload(ByteBuffer bb);

	@Override
	public String toString() {
		
		return 
				"In bits "+inBits+" out bits "+outBits+" GSM balance "+GSMBalance+" battery "+batteryPercentage+"% temp "+deviceTemperature+" voltage "+
				powerVoltage
				;
	}

	@Override
	public int getInBits() {		return inBits;	}

	@Override
	public int getOutBits() {		return outBits;	}

	/**
	 * @return Array of input values in volts
	 */
	@Override
	public double[] getInValue() {		return inValue;	}

	@Override
	public boolean isPowerOk() {		return powerOk;	}

	@Override
	public boolean isBalanceValid() {		return balanceValid;	}

	@Override
	public boolean isCaseOpen() {		return caseOpen;	}

	@Override
	public byte getBatteryPercentage() {		return batteryPercentage;	}

	@Override
	public byte getDeviceTemperature() {		return deviceTemperature;	}

	@Override
	public double getPowerVoltage() {		return powerVoltage;	}

	@Override
	public double getGSMBalance() {		return GSMBalance;	}

}