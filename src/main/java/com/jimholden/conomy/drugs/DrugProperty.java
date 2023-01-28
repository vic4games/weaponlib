package com.jimholden.conomy.drugs;

public class DrugProperty {
	public double weight;
	public float potency;
	public int drugType;
	public boolean synthetic;
	public String name;
	public boolean isPowder;
	
	public DrugProperty(String name, double weight, float potency, int drugType, boolean synthetic, boolean isPowder) {
		this.weight = weight;
		this.potency = potency;
		this.drugType = drugType;
		this.synthetic = synthetic;
		this.name = name;
		this.isPowder = isPowder;
		
	}
	

}
