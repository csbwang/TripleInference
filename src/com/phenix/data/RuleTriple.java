package com.phenix.data;

public class RuleTriple {
	public String entity_1;
	public String entity_2;
	public String relation;
	public RuleTriple(String entity_1, String relation, String entity_2)
	{
		this.entity_1 = entity_1;
		this.relation = relation;
		this.entity_2 = entity_2;
	}
}
