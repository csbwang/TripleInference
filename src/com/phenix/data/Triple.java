package com.phenix.data;

public class Triple {
	public String entity_1;
	public String entity_2;
	public String relation;
	public Triple(String entity_1, String relation, String entity_2)
	{
		this.entity_1 = entity_1;
		this.relation = relation;
		this.entity_2 = entity_2;
	}
}
