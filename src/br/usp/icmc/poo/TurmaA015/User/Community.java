package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public class Community extends AbstractUser {

	public Community(String str){
		super(str);
		maxRentTime = 15;
		maxFiles = 2;
	}

	public String toString(){
		return "Community";
	}
}