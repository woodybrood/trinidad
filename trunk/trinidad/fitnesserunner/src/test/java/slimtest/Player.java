package slimtest;

import java.util.ArrayList;
import java.util.List;

public class Player{
	public String name; 
	public String postCode; 
	public double balance;
	public double getCreditLimit(){
		return balance;
	}
	public Player(){
	}
	public Player(String name, String postCode, double balance){
		this.name=name; this.postCode=postCode; this.balance=balance;
	}
	public static List<Player> players=new ArrayList<Player>();
	public static void addPlayer(String name, String postCode, double balance){
		players.add(new Player(name,postCode,balance));
	}
}
