package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public class Student extends AbstractUser {

	public Student(String str){
		super(str);
		maxRentTime = 15;
		maxFiles = 4;
	}

	public String toString(){
		return "Student";
	}

}