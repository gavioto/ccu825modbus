package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * DeviceInfo packet decoder
 * @author dz
 *
 */

public class CCU825DeviceInfo {

	private final String devType;
	private final String devMod;
	private final String firmWareBuildDate;
	private final String lang;
	private final String IMEI;

	private final short verHardWare;
	private final short verFirmWare;
	private final short verBootLoader;

	private final byte[] serialNumber = new byte[16];
	
	/**
	 * Construct from packet data
	 * @param in Packet payload binary data
	 * @throws CCU825PacketFormatException 
	 */
	
	public CCU825DeviceInfo(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		if( in[0] != CCU825Packet.PKT_TYPE_DEVICEINFO )
			throw new CCU825PacketFormatException("Wrong DeviceInfo payload header byte");
		
		devType = new String( in, 0, 1, 8 ).trim();
		devMod = new String( in, 0, 10, 8 ).trim();

		firmWareBuildDate = new String( in, 0, 25, 12 ).trim();

		lang = new String( in, 0, 37, 4 ).trim();

		IMEI = new String( in, 0, 57, 16 ).trim();
		
		verHardWare = bb.getShort(19);
		verFirmWare = bb.getShort(21);
		verBootLoader = bb.getShort(23);

		System.arraycopy(in, 41, serialNumber, 0, serialNumber.length);		
	}

	
	@Override
	public String toString() {
		
		return 
				"Controller "+devType+" modification "+devMod+" HW "+verHardWare+" FW "+verFirmWare+" ("+firmWareBuildDate+") BL "+
				verBootLoader+" language is "+lang+" IMEI="+IMEI
				;
	}
	
	
	public String getDevType() {
		return devType;
	}

	public String getDevMod() {
		return devMod;
	}

	public String getFirmWareBuildDate() {
		return firmWareBuildDate;
	}

	public String getLang() {
		return lang;
	}

	public String getIMEI() {
		return IMEI;
	}

	public short getVerHardWare() {
		return verHardWare;
	}

	public short getVerFirmWare() {
		return verFirmWare;
	}

	public short getVerBootLoader() {
		return verBootLoader;
	}


	public byte[] getSerialNumber() {
		return serialNumber;
	}
	
}
