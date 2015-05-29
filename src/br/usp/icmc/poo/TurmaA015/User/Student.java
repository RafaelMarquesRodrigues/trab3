package br.usp.icmc.poo.TurmaA015.User;

import java.time.LocalDate;

public class Student extends AbstractUser {

	public Student(String str, LocalDate date){
		super(str, date);
		maxRentTime = 15;
		maxFiles = 4;
		permission = true;
	}

	public String getType(){
		return "Student";
	}
}