package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Teacher extends AbstractUser {

	public Teacher(String name, String id, String nationality, LocalDate date){
		super(name, id, nationality, date);
		maxRentTime = 60;
		maxFiles = 6;
		permission = true;
	}

	public String getType(){
		return "Teacher";
	}
}