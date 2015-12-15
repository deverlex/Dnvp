package com.buzzai.dnvp;

import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

public final class DnvP extends DnvPCore implements IDnvP {

	/**
	 * Class for dnvp - Nguyen Van Do Protocol.
	 * Using packet message and transaction on socket with TCP transaction network layer.
	 * @author Nguyen Van Do
	 */
	private static final long serialVersionUID = 1234567890L;

	public DnvP() { }

	public DnvP(byte[] bytes) {
		super(bytes);
	}

	@Override
	public void setIdPacket(long id) {
		idPacket = id;
		byte[] bt = ByteBuffer.allocate(DnvPConstants.LENGTH_ID_PACKET).putLong(id).array();
		appendByte(bufferStream, DnvPConstants.INDEX_TYPE_ID_PACKET, bt);
	}
	
	@Override
	public long getIdPacket() {
		return idPacket;
	}
	
	@Override
	public String getPackage() {
		return this.strPackage;
	}
	
	@Override
	public String getStrClass() {
		return this.strClass;
	}
	
	@Override
	public String getStrFunc() {
		return this.strFunc;
	}

	@Override
	public void setString(String paramName, String str) {
		setData().put(paramName, str);
	}

	@Override
	public String getString(String paramName) {
		return (String) getValue(paramName);
	}

	@Override
	public void setInt(String paramName, int value) {
		setData().put(paramName, value);
	}

	@Override
	public int getInt(String paramName) {
		return (Integer) getValue(paramName);
	}

	@Override
	public void setDouble(String paramname, double value) {
		setData().put(paramname, value);
	}

	@Override
	public double getDouble(String paramName) {
		return (Double) getValue(paramName);
	}

	@Override
	public void setVector(String paramname, Vector<Object> vect) {
		setData().put(paramname, vect);
	}

	@Override
	public Vector<Object> getVector(String paramName) {
		return (Vector<Object>) getValue(paramName);
	}

	@Override
	public void setBytes(String paramName, byte[] arrByte) {
		setData().put(paramName, arrByte);
	}

	@Override
	public byte[] getBytes(String paramName) {
		return serialize(getValue(paramName));
	}
}
