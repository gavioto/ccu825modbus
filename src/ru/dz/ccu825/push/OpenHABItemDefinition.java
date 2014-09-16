package ru.dz.ccu825.push;

public class OpenHABItemDefinition {
	final private String type;
	final private String name;
	
	public OpenHABItemDefinition(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("Item name=%s type=%s", name, type );
	}
	
	public String getType() {		return type;	}
	public String getName() {		return name;	}
	
}
