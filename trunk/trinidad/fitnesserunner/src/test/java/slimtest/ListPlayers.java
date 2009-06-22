package slimtest;

import java.util.ArrayList;
import java.util.List;
import static util.ListUtility.list;

public class ListPlayers {
	public List<Object> query(){
//		return new ArrayList<Object>(Player.players);
		ArrayList<Object> objects=new ArrayList<Object>();
		for(Player p:Player.players){
			objects.add(list(list("name",p.name), list("balance",p.balance), list("post code",p.postCode)));
		}
		return objects;
	}
}
