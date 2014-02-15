package org.kniftosoft.tigristest;

public class UIDGen 
{

	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//Warning!!! This Class is only for testing - it is not useful for UID generation
	//where UIDs are supposed to be re-used
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	private UIDGen()
	{
		nextUID = 1;
	}
	
	
	public int generateUID()
	{
		int uid = nextUID;
		
		nextUID += 2; //Generate only odd UIDs
		
		return uid;
	}
	
	private int nextUID;
	
	
	private static UIDGen singleton;
	
	public static UIDGen instance()
	{
		if(singleton == null)
		{
			singleton = new UIDGen();
		}
		
		return singleton;
	}
	
}
