package ru.dz.ccu825;

public class CCU825Test {

	public CCU825Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// test rc4 first
		test_rc4();
		
		
		ModBusConnection mc = new ProxyModbus();
		
		try {
			byte[] key = { 0x00, 0x00, 0x00, 0x00 };
			
			CCU825Connection c = new CCU825Connection(mc, key);
			
			c.getSysInfo();
			
		} catch (CCU825Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		
		
		
	}

	private static void test_rc4() {
		byte[] key = { 0x01, 0x02, 0x03, 0x04, 0x05 };
		byte[] inp =  
			{
				(byte)0xb2, 0x39, 0x63, 0x05,  (byte)0xf0, 0x3d, (byte)0xc0, 0x27,   (byte)0xcc, (byte)0xc3, 0x52, 0x4a,  0x0a, 0x11, 0x18, (byte)0xa8
			};
		RC4 enc = new RC4(key );

		byte[] out = enc.encrypt(inp);
		checkzero(out);
		//dumpBytes("enc", out);
	}

	private static void checkzero(byte[] out) {
		for( int i = 0; i < out.length; i++ )
			if( out[i] != 0 )
				throw new RuntimeException("Nonzero array item at " + i);
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
