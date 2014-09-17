package ru.dz.ccu825.data;

/**
 * 
 * Enumeration of sources that can cause arm mode change in CCU825.
 * 
 * @author dz
 *
 */
public enum ArmModeChangeSource 
{

	Button( 0 ),
	TouchMemoryCode( 1 ),
	DTMF( 2 ),
	SMS( 3 ),
	ConfigurationSoftware( 4 ),
	ControlSoftwareCSD( 5 ),
	UserPhoneCall( 6 ),
	Scheduler( 7 ),
	Input( 8 ),
	GPRS( 9 ),
	TouchMemoryUser( 10 ),
	
	
	Unknown( -1 );
	
	int encoding;
	
	private ArmModeChangeSource(int v) {
		this.encoding = v;
	}

	/**
	 * 
	 * Return enum value for corresponding protocol source id.
	 * 
	 * @param c CCU825 protocol arm mode change source id.
	 * @return Enum value.
	 */
	static ArmModeChangeSource forCode(int c)
	{
		for( ArmModeChangeSource v : ArmModeChangeSource.values() )
			if( v.encoding == c )
				return v;
		
		return Unknown;
	}
	
	
	
}
