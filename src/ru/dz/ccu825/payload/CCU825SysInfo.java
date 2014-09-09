package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * SysInfo payload decoder
 * @author dz
 *
 */


public class CCU825SysInfo {

	
	public static final int N_IN = 8;
	
	
	private final byte inBits;
	private final byte outBits;
	
	private final double[] inValue = new double[N_IN];

	private final boolean powerOk;
	private final boolean balanceValid;
	private final boolean caseOpen;

	private final byte batteryPercentage;
	private final byte deviceTemperature;

	private final double powerVoltage;
	private final double GSMBalance;


	public CCU825SysInfo(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		if( in[0] != CCU825Packet.PKT_TYPE_SYSINFO )
			throw new CCU825PacketFormatException("Wrong SysInfo payload header byte");
		
		inBits = in[1];
		outBits = in[18];
		
		for( int i = 0; i < N_IN; i++ )
		{
			inValue [i] = ((double)bb.getShort(i+2)) * 10.0 / 4095; 
		}
		
		{
		byte S1 = in[19];
		powerOk = (S1 & 0x08) != 0;		
		balanceValid = (S1 & 0x04) != 0;
		}
		
		{
		byte S2 = in[20];
		caseOpen = (S2 & 0x01) != 0;
		}
		
		powerVoltage = ((double)in[21])/10.0;
		
		batteryPercentage = in[22];
		
		deviceTemperature = in[23];
		
		GSMBalance = Float.intBitsToFloat( bb.getInt(24) ); 
		
	}

	
	
	@Override
	public String toString() {
		
		return 
				"In bits "+inBits+" out bits "+outBits+" GSM balance "+GSMBalance+" battery "+batteryPercentage+"% temp "+deviceTemperature+" voltage "+
				powerVoltage
				;
	}
		

	public byte getInBits() {
		return inBits;
	}


	public byte getOutBits() {
		return outBits;
	}

	/**
	 * @return Array of input values in volts
	 */
	public double[] getInValue() {
		return inValue;
	}


	public boolean isPowerOk() {
		return powerOk;
	}


	public boolean isBalanceValid() {
		return balanceValid;
	}


	public boolean isCaseOpen() {
		return caseOpen;
	}


	public byte getBatteryPercentage() {
		return batteryPercentage;
	}


	public byte getDeviceTemperature() {
		return deviceTemperature;
	}


	public double getPowerVoltage() {
		return powerVoltage;
	}


	public double getGSMBalance() {
		return GSMBalance;
	}
	
}
