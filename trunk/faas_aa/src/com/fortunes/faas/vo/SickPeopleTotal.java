package com.fortunes.faas.vo;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class SickPeopleTotal extends Model {
	private int sickPeopleTotal;
	
	public SickPeopleTotal() {
	}

	public SickPeopleTotal(int sickPeopleTotal) {
		this.sickPeopleTotal = sickPeopleTotal;
	}

	public int getSickPeopleTotal() {
		return sickPeopleTotal;
	}

	public void setSickPeopleTotal(int sickPeopleTotal) {
		this.sickPeopleTotal = sickPeopleTotal;
	}
	
}
