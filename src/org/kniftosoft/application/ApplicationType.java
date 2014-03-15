package org.kniftosoft.application;



public enum ApplicationType 
{
	
	Maschineapp(1, Maschineapp.class),
	jobapp(2,JobApp.class),
	produktapp(11,Produktapp.class),
	Configapp(20, Config.class);

	
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
		System.out.println("made application by id");
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
