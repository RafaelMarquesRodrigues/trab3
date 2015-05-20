package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public class Teacher extends AbstractUser {

	public Teacher(String str){
		super(str);
		maxRentTime = 60;
		maxFiles = 6;
	}
}