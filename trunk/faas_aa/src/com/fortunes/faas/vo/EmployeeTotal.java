package com.fortunes.faas.vo;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class EmployeeTotal {
	private int peopleTotal;
	
	public EmployeeTotal(){
		
	}

	public EmployeeTotal(int peopleTotal) {
		this.peopleTotal = peopleTotal;
	}

	public int getPeopleTotal() {
		return peopleTotal;
	}

	public void setPeopleTotal(int peopleTotal) {
		this.peopleTotal = peopleTotal;
	}
	
}
