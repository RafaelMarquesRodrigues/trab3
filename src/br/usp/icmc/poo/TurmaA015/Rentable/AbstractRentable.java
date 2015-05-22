package br.usp.icmc.poo.TurmaA015.Rentable;

//classe criada para implementar métodos que são comuns para todos os tipos de objeto "alugáveis"
abstract class AbstractRentable implements Rentable {
	protected String name;
	protected boolean permission;
	protected String rentExpirationDate;
	protected boolean available;
	protected int delay;

	public AbstractRentable(String str){
		name = str;
		rentExpirationDate = "null";
		delay = 0;
		available = true;
	}
	
	public boolean needsPermission(){
		return permission;
	}

	public void setRentExpirationDate(String date){
		rentExpirationDate = date;
	}

	public String getRentExpirationDate(){
		return rentExpirationDate;
	}

	public void setDelay(int n){
		delay = n;
	}

	public int getDelay(){
		return delay;
	}

	public void removeDelay(){
		delay = 0;
	}

	public void rent(){
		available = false;
	}

	public void refund(){
		available = true;
	}

	public boolean isAvailable(){
		return available;
	}

	//nome do livro/anotação
	public String getName(){
		return name;
	}

	public String getType(){
		return "none";
	}

	public String toString(){
		return name;
	}
}