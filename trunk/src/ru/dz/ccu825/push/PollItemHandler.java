package ru.dz.ccu825.push;

public abstract class PollItemHandler
{
	/**
	 * Called with name and value of item read from OpenHAB. Must
	 * process value somehow (send it along). 
	 * 
	 * @param item item's name
	 * @param value item's value
	 */
	public abstract void transfer(String item, String value);

}