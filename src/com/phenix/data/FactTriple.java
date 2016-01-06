package com.phenix.data;

public class FactTriple {
	public Entity entity_1;
	public Entity entity_2;
	public String relation;
	public FactTriple(Entity entity_1, String relation, Entity entity_2)
	{
		this.entity_1 = entity_1;
		this.relation = relation;
		this.entity_2 = entity_2;
	}
}
