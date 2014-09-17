package ru.dz.ccu825.payload;

import java.util.Iterator;

import ru.dz.ccu825.data.ArmModeChange;

/**
 * 
 * List of events as received from controller.
 * 
 * @author dz
 *
 */
public interface ICCU825Events {

	/**
	 * Events reply packet contains current sysinfo as well.
	 * 
	 * @return SysInfo
	 */
	public abstract ICCU825SysInfo getSysInfo();
	
	/**
	 * To iterate through all of the events.
	 * 
	 * @return Iterator
	 */
	public abstract Iterator<Byte> iterator();

	/** Who armed last. */
	public abstract ArmModeChange getArmDetail();

	
	/** Who disarmed last. */
	public abstract ArmModeChange getDisarmDetail();

	
	/** Who switched to protect mode last. */
	public abstract ArmModeChange getProtectDetail();
	
	
	public abstract String toString();


}