package com.buzzai.dnvp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */
class DnvPInputStream implements Runnable {

	////alway listener request/response from InputStream
	private DnvPTransactionCore dnvpTrans;
	private byte[]  packetStream;
	private static final short countFilterPacket = 20;
	private ConcurrentHashMap<String, DnvPStorage> mapStorage = new ConcurrentHashMap<>();
	
	public DnvPInputStream(DnvPTransactionCore dnvpTrans) {
		this.dnvpTrans = dnvpTrans;
	}
	
	@Override
	public void run() {
		int count = 0;
		while (dnvpTrans.isOpen()) {
			try {
				deserializeStream(dnvpTrans.getInputStream());
				++count; // number of input - filter input packet
				if(count == countFilterPacket) {
					for(String key : mapStorage.keySet()) {
						DnvPStorage stg = mapStorage.get(key);
						if(stg.checkRemove()) {
							mapStorage.remove(key);
						}
					}
					count = 0; 
				}
			} catch (IOException e) {
				DnvPException.DnvPExceptionStatic("InputStream IO exception - DnvPInputStream", e);
			}
		}
	}
	
	private void deserializeStream(InputStream istream) throws IOException {
		byte[] temp = new byte[DnvPConstants.MAX_REQUEST_SIZE];
		synchronized (istream) {
			int lenght = istream.read(temp);
			if (temp[DnvPConstants.INDEX_TYPE_PACKET] == DnvPConstants.FLAG_PACKET_REQUEST) {
				if (temp[DnvPConstants.INDEX_FLAG_DATA_REQUEST] == DnvPConstants.FLAG_PACKET_NO_DATA
						|| (lenght < DnvPConstants.MAX_REQUEST_SIZE
								&& temp[DnvPConstants.INDEX_FLAG_CONTENT_REQUEST] == DnvPConstants.FLAG_DATA_CLONE)) {
					packetStream = new byte[lenght];
					System.arraycopy(temp, 0, packetStream, 0, lenght);
					DnvP dnvp = new DnvP(packetStream);
					dnvpTrans.addRequest(dnvp);
				} else {
					byte[] buff = new byte[lenght];
					System.arraycopy(temp, 0, buff, 0, lenght);
					longRequestPacket(buff);
				}
			} else {
				if (lenght < DnvPConstants.MAX_RESPONSE_SIZE
						&& temp[DnvPConstants.INDEX_FLAG_CONTENT_RESPONSE] == DnvPConstants.FLAG_DATA_CLONE) {
					packetStream = new byte[lenght];
					System.arraycopy(temp, 0, packetStream, 0, lenght);
					DnvP dnvp = new DnvP(packetStream);
					dnvpTrans.addResponse(dnvp);
				} else {
					byte[] buff = new byte[lenght];
					System.arraycopy(temp, 0, buff, 0, lenght);
					longResponsePacket(buff);
				}
			}
		}
	}
	
	private void longRequestPacket(byte[] bytes) {
		String idPacket = getiDPacket(bytes); 
		DnvPStorage stg = mapStorage.get(idPacket);
		if (stg != null) {
			if(stg.checkRemove()) {
				mapStorage.remove(idPacket);
			} else {
				stg.appendByte(bytes);
				stg.setFlagContent(bytes[DnvPConstants.INDEX_FLAG_CONTENT_REQUEST]); // bytes[9] with response
				if(stg.ckeckComplete()) {
					DnvP dnvp = new DnvP(stg.getStorage());
					dnvpTrans.addRequest(dnvp);
				}
			}
		} else {
			stg = new DnvPStorage(bytes, getLeng(bytes, DnvPConstants.FLAG_PACKET_REQUEST), DnvPConstants.FLAG_PACKET_REQUEST);
			mapStorage.put(idPacket, stg);
		}
	}
	
	/**
	 * @param bytes
	 */
	private void longResponsePacket(byte[] bytes) {
		String idPacket = getiDPacket(bytes);
		DnvPStorage stg = mapStorage.get(idPacket);
		if (stg != null) {
			if(stg.checkRemove()) {
				mapStorage.remove(idPacket);
			} else {
				stg.appendByte(bytes);
				stg.setFlagContent(bytes[DnvPConstants.INDEX_FLAG_CONTENT_RESPONSE]); // bytes[100] with response
				if(stg.ckeckComplete()) {
					DnvP dnvp = new DnvP(stg.getStorage());
					dnvpTrans.addResponse(dnvp);
				}
			}
		} else {
			stg = new DnvPStorage(bytes, getLeng(bytes, DnvPConstants.FLAG_PACKET_RESPONSE), DnvPConstants.FLAG_PACKET_RESPONSE);
			mapStorage.put(idPacket, stg);
		}
	}
	
	private int getLeng(byte[] bytes, byte flagType) {
		byte[] leng = new byte[DnvPConstants.LENGTH_CONTENT];
		if (flagType == DnvPConstants.FLAG_PACKET_REQUEST) {
			System.arraycopy(bytes, DnvPConstants.INDEX_LENGTH_CONTENT_REQUEST, leng, 0, DnvPConstants.LENGTH_CONTENT);
		} else {
			System.arraycopy(bytes, DnvPConstants.INDEX_LENGTH_CONTENT_RESPONSE, leng, 0, DnvPConstants.LENGTH_CONTENT);
		}
		return ByteBuffer.wrap(leng).getInt();
	}
	
	private String getiDPacket(byte[] bytes) {
		byte[] id = new byte[DnvPConstants.LENGTH_ID_PACKET];
		System.arraycopy(bytes, DnvPConstants.INDEX_TYPE_ID_PACKET, id, 0, DnvPConstants.LENGTH_ID_PACKET);
		long idL = ByteBuffer.wrap(id).getLong();
		return Long.toString(idL);
	}
}
