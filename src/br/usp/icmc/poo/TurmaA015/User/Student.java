package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Student extends AbstractUser {

	public Student(String name, String id, String nationality, LocalDate date){
		super(name, id, nationality, date);
		maxRentTime = 15;
		maxFiles = 4;
		permission = true;
	}

	public String getType(){
		return "Student";
	}
}