package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.binutil.BitPrint;
import ru.dz.binutil.BitRange;
import ru.dz.ccu825.CCU825Test;
import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;

/**
 * Set of general device parameters.
 * <p>
 * <li>Serial number. 
 * @author dz
 *
 */
public class MercuryInfo extends MercuryRequest {
	private byte deviceAddress;
	private byte[] serial;
	private byte[] version;
	//private byte[] placement;
	private int iCoeff;
	private int uCoeff;
	private byte[] deviceModification;
	
	public MercuryInfo(Mercury230Connection c) throws IOException, Mercury230ProtocolException 
	{
		byte[] packet;
		
		serial = getParameter(c,0);
		//c.sendParameterReadRequestPacket(0);
		//serial = c.readNonRcPacket().getPayload();

		packet = getParameter(c,2);
		//c.sendParameterReadRequestPacket(2);
		//packet = c.readNonRcPacket().getPayload();

		iCoeff = ((int)packet[2]) & 0xFF;
		iCoeff <<= 8;
		iCoeff |= ((int)packet[3]) & 0xFF;
		
		uCoeff = ((int)packet[0]) & 0xFF;
		uCoeff <<= 8;
		uCoeff |= ((int)packet[1]) & 0xFF;
		
		version = getParameter(c,3);
		//c.sendParameterReadRequestPacket(3);
		//version = c.readNonRcPacket().getPayload();
		

		packet = getParameter(c,5);
		//c.sendParameterReadRequestPacket(5);
		//packet = c.readNonRcPacket().getPayload();
		deviceAddress = packet[1];
		
		//c.sendParameterReadRequestPacket(9);
		//packet = c.readNonRcPacket().getPayload();
		//CCU825Test.dumpBytes("Flags", packet);
		
		//c.sendParameterReadRequestPacket(0x0A);
		//packet = c.readNonRcPacket().getPayload();
		//CCU825Test.dumpBytes("State", packet);
		
		//c.sendParameterReadRequestPacket(0x0B);
		//placement = c.readNonRcPacket().getPayload();

		deviceModification = getParameter(c,0x12);
		//c.sendParameterReadRequestPacket(0x12);
		//deviceModification = c.readNonRcPacket().getPayload();

	}

	private static byte[] getParameter(Mercury230Connection c, int nParam) throws IOException, Mercury230ProtocolException
	{
		c.sendParameterReadRequestPacket(nParam);
		return c.readNonRcPacket().getPayload();		
	}

	public String getVersion()
	{
		return String.format("%02d.%02d.%02d", version[0], version[1], version[2]  );
	}
	
	public String getSerial()
	{
		return String.format("%02X%02X%02X%02X %02d.%02d.20%02d", 
				serial[0], serial[1], serial[2], serial[3],
				serial[4], serial[5], serial[6]
						);
	}
	
	static private final BitPrint bp = new BitPrint(new BitRange[] {
			new BitRange(1,7,"1-way","2-way"),
			new BitRange(1,6,"40degC","20degC"),
			new BitRange(1,5,"AvgPowerProfile"),
			new BitRange(1,4,"1 phase","3 phase"),
			//new BitRange(1,0,"постоянная счётчика",4),
			new BitRange(2,7,"Module sum","Signed sum"),
			new BitRange(2,6,"Int tariff","Ext tariff"),
			new BitRange(2,4,"type",2),
			new BitRange(2,0,"variant",4),
			//new BitRange(3,7,"Mem3=131x8","Mem3=65,5x8"),
			new BitRange(3,6,"PLM"),
			new BitRange(3,5,"GSM"),
			new BitRange(3,4,"IRDA"),
			new BitRange(3,2,"Interface",2),
			new BitRange(3,1,"ExtPower"),
			new BitRange(3,0,"El.Seal"),
			new BitRange(4,3,"2nd RS485"),
			new BitRange(4,2,"IntIfacePower"),
			new BitRange(4,1,"PKE"),
			new BitRange(4,0,"A+ per phase"),			
	});
	
	public String getDeviceModificationString()
	{
		
		
		return bp.print(deviceModification);
	}
	
	@Override
	public String toString() {
		return String.format("Addr=%d; FW Ver=%s; Serial=%s; Coeff U=%d, I=%d; %nModification=%s", deviceAddress, getVersion(),  getSerial(), uCoeff, iCoeff, getDeviceModificationString() );
	}
	

	
}
