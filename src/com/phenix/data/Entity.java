package com.phenix.data;

public class Entity {
	public String id;
	public String value;
	
	public Entity(String id, String value)
	{
		this.id = id;
		this.value = value;
	}
	
	public boolean equals(Entity entity)
	{
		if((this.id.equals(entity.id)) && (this.value.equals(entity.value)))
			return true;
		return false;
	}
}
