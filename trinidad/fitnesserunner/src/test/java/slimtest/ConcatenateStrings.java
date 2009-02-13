package slimtest;

public class ConcatenateStrings {
	public String first;
	public String second;
	public String concatenate(){
		return first+" "+second;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
}
