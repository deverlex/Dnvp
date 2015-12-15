package com.buzzai.dnvp;

import java.net.Socket;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

public final class DnvPTransaction extends DnvPTransactionCore implements IDnvPTransaction {

	public DnvPTransaction() {
		super();
	}
	
	public DnvPTransaction(Socket socket) {
		super(socket);
	}
	
	@Override
	public void setStrPackage(String strPackage) {
		this.strPackage = strPackage;
	}
	
	@Override
	public String getStrPackage() {
		return this.strPackage;
	}
	
	@Override
	public DnvP sendRequest(String strClass, String strFunc) {
		DnvP dnvp = new DnvP();
		dnvp.setIdPacket(System.currentTimeMillis());
		dnvp.setPackage(strPackage);
		dnvp.setFlag(DnvPConstants.FLAG_PACKET_NO_DATA);
		dnvp.setStrClass(strClass);
		dnvp.setStrFunc(strFunc);
		return sendRequest(dnvp);
	}

	@Override
	public DnvP sendRequest(String strClass, String strFunc, DnvP dnvp) {
		if(dnvp.idPacket == 0) {
			dnvp.setIdPacket(System.currentTimeMillis());
		}
		dnvp.setPackage(strPackage);
		dnvp.setStrClass(strClass);
		dnvp.setStrFunc(strFunc);
		dnvp.setFlag(DnvPConstants.FLAG_PACKET_DATA);
		return sendRequest(dnvp);
	}
	
	@Override
	public void sendResponse(DnvP dnvp) {
		dnvp.setTypePacket(DnvPConstants.FLAG_PACKET_RESPONSE);
		send(dnvp);
	}

	@Override
	public void pushPacket(String strClass, String strFunc, DnvP dnvp) {
		if(dnvp.idPacket == 0) {
			dnvp.setIdPacket(System.currentTimeMillis());
		}
		dnvp.setPackage(strPackage);
		dnvp.setStrClass(strClass);
		dnvp.setStrFunc(strFunc);
		dnvp.setFlag(DnvPConstants.FLAG_PACKET_DATA);
		pushPacket(dnvp);
	}
	
}
