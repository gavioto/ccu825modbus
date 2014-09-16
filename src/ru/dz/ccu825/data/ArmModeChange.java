package ru.dz.ccu825.data;

import java.nio.charset.Charset;

public class ArmModeChange 
{
	static final Charset ascii = Charset.forName("ascii");
	
	private ArmModeChangeSource src;
	private String ident;

	public ArmModeChange(byte[] data, int startPos) {
		//ByteBuffer bb = ByteBuffer.wrap(data, startPos, 17);
		
		byte type = data[startPos];
		src = ArmModeChangeSource.forCode(type);
		
		ident = null;
		
		switch(type)
		{
		case 1:
			byte[] sub = new byte[16];
			System.arraycopy(data, startPos, sub, 0, 16);
			ident = javax.xml.bind.DatatypeConverter.printHexBinary(sub); 
			break;
		case 10:
		case 2:
		case 3:
		case 5:
		case 6:
			ident = new String( data, startPos+1, 16, ascii ).trim();
			break;
			
		}
	}

	public ArmModeChangeSource getSrc() {
		return src;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		if(ident != null)
			return "Arm mode change src="+src+" ("+ident+")";
		else
			return "Arm mode change src="+src;
	}

}
