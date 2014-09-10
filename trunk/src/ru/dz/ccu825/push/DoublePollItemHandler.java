package ru.dz.ccu825.push;

import java.util.logging.Logger;


public abstract class DoublePollItemHandler extends PollItemHandler {
	private final static Logger log = Logger.getLogger(DoublePollItemHandler.class.getName());

	@Override
	public void transfer(String item, String value) {
		
		try {
			double v = Double.parseDouble(value);
			transfer(item, v);
		} catch (NumberFormatException e) {
			log.severe(e.getMessage());
			//e.printStackTrace();
		}

	}

	abstract public void transfer(String item, Double value);
	
	
}
