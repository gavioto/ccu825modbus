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


public class CCU825SysInfoEx implements ICCU825SysInfo {

	
	public static final int N_IN = 16;
	
	
	private final int inBits;
	private final int outBits;
	
	private final double[] inValue = new double[N_IN];

	private final boolean powerOk;
	private final boolean balanceValid;
	private final boolean caseOpen;

	private final byte batteryPercentage;
	private final byte deviceTemperature;

	private final double powerVoltage;
	private final double GSMBalance;


	public CCU825SysInfoEx(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		if( in[0] != CCU825Packet.PKT_TYPE_SYSINFO_EX )
			throw new CCU825PacketFormatException("Wrong SysInfoEx payload header byte");
		
		inBits = bb.getShort(1);		
		outBits = in[35];
		
		for( int i = 0; i < N_IN; i++ )
		{
			inValue [i] = ((double)bb.getShort(i+3)) * 10.0 / 4095; 
		}
		
		// TODO extract as sub-object
		{
		byte S1 = in[36];
		powerOk = (S1 & 0x08) != 0;		
		balanceValid = (S1 & 0x04) != 0;
		}
		
		{
		byte S2 = in[37];
		caseOpen = (S2 & 0x01) != 0;
		}
		
		powerVoltage = ((double)in[38])/10.0;
		
		batteryPercentage = in[39];
		
		deviceTemperature = in[40];
		
		GSMBalance = Float.intBitsToFloat( bb.getInt(41) ); 
		
	}

	
	
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



	@Override
	public int nInputs() {
		return N_IN;
	}
	
}
