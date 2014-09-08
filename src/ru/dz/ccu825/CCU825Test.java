package ru.dz.ccu825;

public class CCU825Test {

	public CCU825Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {				
		
		ModBusConnection mc = new ProxyModbus();

		byte[] key = { 0x00, 0x00, 0x00, 0x00 };
		
		CCU825Connection c = new CCU825Connection(mc, key);
		
		try {
		
			CCU825ReturnCode protocolRC = c.connect();
			System.out.print("RC = " + protocolRC ); // TODO move out
					
			c.getSysInfo();
			
		} catch (CCU825Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		
		
		
	}



	public static void dumpBytes(String string, byte[] b) 
	{
		System.err.println(string + ", len = " + b.length);
		
		int p = 0;
		
		while( p < b.length )
		{
			if( (p % 16) == 0 )
				System.err.println("");
			
			byte cb = b[p++];
			
			System.err.print( String.format("%02X ", cb) );
		}
		
		
		System.err.println("");
		System.err.println("--");
	}

}
