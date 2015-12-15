package com.buzzai.dnvp;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

public interface IDnvPTransaction {
	//interface for request action
	public DnvP sendRequest(String strClass,String strFunc);
	
	public DnvP sendRequest(String strClass,String strFunc, DnvP dnvp);
	
	public void sendResponse(DnvP dnvp);
	
	public void pushPacket(String strClass,String strFunc,DnvP dnvp);
	
	public void setStrPackage(String strPackage);
	
	public String getStrPackage();

}
