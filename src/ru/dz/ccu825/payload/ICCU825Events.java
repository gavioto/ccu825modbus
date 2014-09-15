package ru.dz.ccu825.payload;

import java.util.Iterator;

public interface ICCU825Events {

	public abstract ICCU825SysInfo getSysInfo();
	
	/**
	 * To iterate through all of the events.
	 * @return Iterator
	 */
	public abstract Iterator<Byte> iterator();

	public abstract String toString();


}