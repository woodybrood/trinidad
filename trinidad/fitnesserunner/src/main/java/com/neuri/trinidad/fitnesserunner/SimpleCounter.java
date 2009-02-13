package com.neuri.trinidad.fitnesserunner;
import fit.Counts;
import fit.FixtureListener;
import fit.Parse;

public class SimpleCounter implements FixtureListener{
	private Counts counts=new Counts();
	public SimpleCounter(){
	}
	public void tableFinished(Parse table) { 	
	}
	public void tablesFinished(Counts count) {
		counts.tally(count);		
	}
	public Counts getCounts() {
		return counts;
	}
}