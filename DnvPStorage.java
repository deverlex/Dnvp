package com.buzzai.dnvp;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */
class DnvPStorage {
	
	private byte[] bufferPacket; // 1MB for packet
	private byte flag = 0; //  flag danh dau ban tin da ket thuc chua (start/extend/end)
	private int index = 0; // danh dau vi tri goi tin hien tai
	private int lengStg = 0; //do dai cua ban tin
	private int typePacket; ///request or response
	
	private long timeStartUp;
	
	public DnvPStorage() {}
	
	public DnvPStorage(byte[] bytes, int lengContent, int typePacket) {
		// TODO Auto-generated constructor stub
		this.lengStg = lengContent;
		if(typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			bufferPacket = new byte[lengStg + DnvPConstants.HEADER_REQUEST];
		} else {
			bufferPacket = new byte[lengStg + DnvPConstants.HEADER_RESPONSE];
		}
		this.typePacket = typePacket;
		createPacket(bytes);
		timeStartUp = System.currentTimeMillis();
	}
	
	public void createPacket(byte[] bytes) {
		System.arraycopy(bytes, 0, bufferPacket, 0, bytes.length);
		index += bytes.length;
	}
	
	public void appendByte(byte[] temp) {
		if (typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			System.arraycopy(temp, DnvPConstants.HEADER_REQUEST, bufferPacket, index,
					temp.length - DnvPConstants.HEADER_REQUEST);
			index += temp.length - DnvPConstants.HEADER_REQUEST;
		} else {
			System.arraycopy(temp, DnvPConstants.HEADER_RESPONSE, bufferPacket, index,
					temp.length - DnvPConstants.HEADER_RESPONSE);
			index += temp.length - DnvPConstants.HEADER_RESPONSE;
		}
	}
	
	public boolean ckeckComplete() {
		return (flag == DnvPConstants.FLAG_DATA_END);
	}
	
	public boolean checkRemove() {
		return (System.currentTimeMillis() - timeStartUp > 40000);
	}
	
	public void setFlagContent(byte flagPk) {
		this.flag = flagPk;
	}
	
	public byte[] getStorage() {
		return this.bufferPacket;
	}
	
}
