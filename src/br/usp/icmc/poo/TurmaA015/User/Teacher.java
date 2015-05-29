package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Teacher extends AbstractUser {

	public Teacher(String str, LocalDate date){
		super(str, date);
		maxRentTime = 60;
		maxFiles = 6;
		permission = true;
	}

	public String getType(){
		return "Teacher";
	}
}