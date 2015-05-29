package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Community extends AbstractUser {

	public Community(String str, LocalDate date){
		super(str, date);
		maxRentTime = 15;
		maxFiles = 2;
		permission = false;
	}

	public String getType(){
		return "Community";
	}
}