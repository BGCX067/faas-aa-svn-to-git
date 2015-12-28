package net.fortunes.core;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Model implements Serializable{
	
	@Override
	public String toString() {
		return "";
	}
	
}
