package slimtest;

import java.math.BigDecimal;

public class CreatePlayers {
	static{
		fitnesse.slim.Slim.addConverter(BigDecimal.class, new BigDecimalConverter());
	}
	public void playerRegistersWithNamePostcodeAndBalance(String name, String postCode, BigDecimal balance){
		Player.addPlayer(name, postCode, balance.doubleValue());
	}
	public boolean checkMe(int x){
		return x==5;
	}
	public int getDoubleIs(int x){
		return x*2;
	}
	
}
