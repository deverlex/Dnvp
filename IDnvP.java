package com.buzzai.dnvp;

import java.io.Serializable;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

public interface IDnvP extends Serializable {
	
	///Header
	public void setIdPacket(long id);
	public long getIdPacket();
	public String getPackage();
	public String getStrClass();
	public String getStrFunc();
	
	///Content
	public void setString(String paramName, String str);
	public String getString(String paramName);
	public void setInt(String paramName, int value);
	public int getInt(String paramName);
	public void setDouble(String paramname, double value);
	public double getDouble(String paramName);
	public void setVector(String paramname,Vector<Object> vect);
	public Vector<Object> getVector(String paramName);
	public void setBytes(String paramName,byte[] arrByte);
	public byte[] getBytes(String paramName);
	
}
