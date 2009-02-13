package com.neuri.trinidad;

public class InMemoryTestImpl implements Test{
	private String name;
	private String content;
	public String getName() {
		return name;
	}
	public String getContent() {
		return content;
	}
	public InMemoryTestImpl(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}	
}
