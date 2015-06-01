package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Community extends AbstractUser {

	public Community(String name, String id, String nationality, LocalDate date){
		super(name, id, nationality, date);
		maxRentTime = 15;
		maxFiles = 2;
		permission = false;
	}

	public String getType(){
		return "Community";
	}
}