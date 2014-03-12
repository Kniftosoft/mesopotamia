package org.kniftosoft.application;



public enum ApplicationType 
{
	
	Test(1, Test.class);

	
	private int typeID;
	private Class<?> appClass;
	
	private ApplicationType(int typeID, Class<?> appClass)
	{
		this.typeID = typeID;
		this.appClass = appClass;
	}
	
	
	public int getTypeID()
	{
		return typeID;
	}
	
	public Class<?> getAppClass()
	{
		return appClass;
	}
	
	public static ApplicationType byID(int id)
	{
		System.out.println("male application by id");
		for(ApplicationType a : ApplicationType.values())
		{
			if(a.getTypeID() == id)
			{
				return a;
			}
		}
		
		return null;
	}
}
