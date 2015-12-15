package com.buzzai.dnvp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

abstract class DnvPCore {

	////////////// buffer of stream
	protected byte[] bufferStream = new byte[DnvPConstants.HEADER_REQUEST]; 
	private Queue<byte[]> queueBuffer = new ArrayDeque<>();
	private HashMap<Object,Object> hashMap = new HashMap<Object,Object>();
	protected byte typePacket; // loai request hay response
	protected long idPacket = 0;
	protected byte flagPacket = 0; // loai co data hay k co data
	protected String strPackage = "";
	protected String strClass = "";
	protected String strFunc = "";
	private static short timeDelay = 10;
	///////////////////////////////////////////
	/// REQUEST - PACKET         ||id||flag||strPackage||strClass||strFunc||flagContent||lengthContent||Content||
	/// RESPONSE - PACKET        ||id||flag||flagContent||lengthContent||Content||
	/// REQUEST - PACKET NO DATA ||id||flag||strPackage||strClass||strFunc||
	//////////////////////////////////////////
	
	protected DnvPCore() {}

	protected DnvPCore(byte[] bytes) {
		parseMessage(bytes);
	}

	/**
	 * append byte arrays to bufferStream
	 * @param dest
	 * @param destPos
	 * @param src
	 * @return
	 */

	protected byte[] appendByte(byte[] dest, int destPos, byte[] src) {
		System.arraycopy(src, 0, dest, destPos, src.length);
		return dest;
	}
	
	protected HashMap<Object,Object> setData() {
		return this.hashMap;
	}
	
	protected Object getValue(String paramName){
		return hashMap.get(paramName);
	}
	
	/******************************************************************/
	/////////////////////  FOR HEADER
	/******************************************************************/
	
	protected void setTypePacket(byte type) {
		typePacket = type;
		byte[] flag = { type };
		appendByte(bufferStream, DnvPConstants.INDEX_TYPE_PACKET, flag);
	}
	
	protected byte getTypePacket(byte[] bytes) {
		return bytes[DnvPConstants.INDEX_TYPE_PACKET];
	}
	
	protected long getiDMessage(byte[] bytes) {
		byte[] id = new byte[DnvPConstants.LENGTH_ID_PACKET];
		System.arraycopy(bytes, DnvPConstants.INDEX_TYPE_ID_PACKET, id, 0, DnvPConstants.LENGTH_ID_PACKET);
		return ByteBuffer.wrap(id).getLong();
	}
	
	protected void setFlag(byte bt) {
		flagPacket = bt;
		byte[] flag = { bt };
		appendByte(bufferStream, DnvPConstants.INDEX_FLAG_DATA_REQUEST, flag);
	}

	protected byte getFlag(byte[] bytes) {
		byte flag = bytes[DnvPConstants.INDEX_FLAG_DATA_REQUEST];
		return flag;
	}
	
	protected void setPackage(String strPackage) {
		byte[] bytePackage = strPackage.getBytes();
		appendByte(bufferStream, DnvPConstants.INDEX_PACKAGE_REQUEST, bytePackage);
	}
	
	protected String getPackage(byte[] bytes) {
		byte[] bytePackage = new byte[DnvPConstants.LENGTH_NAME_PACKAGE];
		System.arraycopy(bytes, DnvPConstants.INDEX_PACKAGE_REQUEST, bytePackage, 0, DnvPConstants.LENGTH_NAME_PACKAGE);
		String strPackage = new String(bytePackage);
		return strPackage;
	}
	
	protected void setStrClass(String strClass) {
		byte[] bt = strClass.getBytes();
		appendByte(bufferStream, DnvPConstants.INDEX_CLASS_REQUEST, bt);
	}
	
	protected String getStrClass(byte[] bytes) {
		byte[] byteClass = new byte[DnvPConstants.LENGTH_NAME_CLASS];
		System.arraycopy(bytes, DnvPConstants.INDEX_CLASS_REQUEST, byteClass, 0, DnvPConstants.LENGTH_NAME_CLASS);
		String strClass = new String(byteClass);
		return strClass;
	}
	
	protected void setStrFunc(String strFunc) {
		byte[] bt = strFunc.getBytes();
		appendByte(bufferStream, DnvPConstants.INDEX_FUNCTION_REQUEST, bt);
	}
	
	protected String getStrFunc(byte[] bytes) {
		byte[] byteFunc = new byte[DnvPConstants.LENGTH_NAME_FUNCTION];
		System.arraycopy(bytes, DnvPConstants.INDEX_FUNCTION_REQUEST, byteFunc, 0, DnvPConstants.LENGTH_NAME_FUNCTION);
		String strFunc = new String(byteFunc);
		return strFunc;
	}
	
	protected void setFlagContent(byte bt) {
		byte[] fgContent = { bt };
		// TODO chen vao byte thu 120
		if(typePacket ==  DnvPConstants.FLAG_PACKET_REQUEST) {
			appendByte(bufferStream, DnvPConstants.INDEX_FLAG_CONTENT_REQUEST, fgContent);
		} else if(typePacket == DnvPConstants.FLAG_PACKET_RESPONSE) {
			appendByte(bufferStream, DnvPConstants.INDEX_FLAG_CONTENT_RESPONSE, fgContent);
		}
	}
	
	protected byte getFlagContent(byte[] bytes) {
		if (typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			byte flagCt = bytes[DnvPConstants.INDEX_FLAG_CONTENT_REQUEST];
			return flagCt;
		} else {
			byte flagCt = bytes[DnvPConstants.INDEX_FLAG_CONTENT_RESPONSE];
			return flagCt;
		}
	}
	
	protected void setLengContent(int leng) {
		byte[] bt = ByteBuffer.allocate(DnvPConstants.LENGTH_CONTENT).putInt(leng).array();
		if (typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			appendByte(bufferStream, DnvPConstants.INDEX_LENGTH_CONTENT_REQUEST, bt);
		} else if (typePacket == DnvPConstants.FLAG_PACKET_RESPONSE) {
			appendByte(bufferStream, DnvPConstants.INDEX_LENGTH_CONTENT_RESPONSE, bt);
		}
	}
	
	protected int getLengContent(byte[] bytes) {
		byte[] leng = new byte[DnvPConstants.LENGTH_CONTENT];
		if (typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			System.arraycopy(bytes, DnvPConstants.INDEX_LENGTH_CONTENT_REQUEST, leng, 0, DnvPConstants.LENGTH_CONTENT);
		} else {
			System.arraycopy(bytes, DnvPConstants.INDEX_LENGTH_CONTENT_RESPONSE, leng, 0, DnvPConstants.LENGTH_CONTENT);
		}
		return ByteBuffer.wrap(leng).getInt();
	}
	
	/**************************************************************/
	/////////////////////for content
	/**************************************************************/
	
	private void parseMessage(byte[] msg) {
		typePacket = getTypePacket(msg);
		idPacket = getiDMessage(msg);
		flagPacket = getFlag(msg);
		if(typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			strPackage = getPackage(msg);
			strClass = getStrClass(msg);
			strFunc = getStrFunc(msg);
			if(flagPacket == DnvPConstants.FLAG_PACKET_DATA) {
				hashMap = getContentRequest(msg);
			}
		} else if(typePacket == DnvPConstants.FLAG_PACKET_RESPONSE) {
			hashMap = getContentResponse(msg);
		}
		
	}

	private HashMap<Object, Object> getContentRequest(byte[] msg) {
		int lengBuff = msg.length - DnvPConstants.HEADER_REQUEST;
		byte[] buffer = new byte[lengBuff];
		System.arraycopy(msg, DnvPConstants.HEADER_REQUEST, buffer, 0, lengBuff);
		return (HashMap<Object, Object>) deserialize(buffer);
	}
	
	private HashMap<Object, Object> getContentResponse(byte[] msg) {
		int lengBuff = msg.length - DnvPConstants.HEADER_RESPONSE;
		byte[] buffer = new byte[lengBuff];
		System.arraycopy(msg, DnvPConstants.HEADER_RESPONSE, buffer, 0, lengBuff);
		return (HashMap<Object, Object>) deserialize(buffer);
	}
	
	protected byte[] serialize(Object obj) {
		byte[] result;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(obj);
		    result = out.toByteArray();
		    return result;
		} catch(IOException e){
			DnvPException.DnvPExceptionStatic("Serialize Object to byte[]", e);
		}
	    return null;
	}
	protected Object deserialize(byte[] data) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
		    ObjectInputStream is = new ObjectInputStream(in);
		    Object obj = is.readObject();
		    return obj;
		} catch (IOException e) {
			DnvPException.DnvPExceptionStatic("Deserialize byte[] to Object - IO Exception", e);
		} catch (ClassNotFoundException e) {
			DnvPException.DnvPExceptionStatic("Deserialize byte[] to Object - Class Not Found", e);
		}
	    return null;
	}
	
	/************************************************************/
	///////////////////for process outputStream
	/// set array byte, analyst array bit for get information
	protected void load(byte[] bytes) {
		int stack = (int) bytes.length / DnvPConstants.MAX_CONTENT_SIZE;
		setLengContent(bytes.length);
		int index = 0;
		if (typePacket == DnvPConstants.FLAG_PACKET_REQUEST) {
			loadRequest(bytes, stack, index);
		} else {
			loadResponse(bytes, stack, index);
		}

	}
	
	private void loadRequest(byte[] bytes,int stack,int index) {
		while (stack > 0) {
			if (index == 0) {
				setFlagContent(DnvPConstants.FLAG_DATA_BEGIN);
			} else {
				setFlagContent(DnvPConstants.FLAG_DATA_EXTEND);
			}
			byte[] tempBuffer = new byte[DnvPConstants.MAX_REQUEST_SIZE];
			System.arraycopy(bytes, index, tempBuffer, DnvPConstants.HEADER_REQUEST,
					DnvPConstants.MAX_CONTENT_SIZE);
			System.arraycopy(bufferStream, 0, tempBuffer, 0, DnvPConstants.HEADER_REQUEST);
			queueBuffer.add(tempBuffer);
			index += DnvPConstants.MAX_CONTENT_SIZE;
			stack -= 1;
		}
		if (index == 0) {
			setFlagContent(DnvPConstants.FLAG_DATA_CLONE);
		} else {
			setFlagContent(DnvPConstants.FLAG_DATA_END);
		}
		byte[] endBuffer = new byte[bytes.length - index + DnvPConstants.HEADER_REQUEST];
		System.arraycopy(bytes, index, endBuffer, DnvPConstants.HEADER_REQUEST, bytes.length - index);
		System.arraycopy(bufferStream, 0, endBuffer, 0, DnvPConstants.HEADER_REQUEST);
		queueBuffer.add(endBuffer);
	}
	
	private void loadResponse(byte[] bytes,int stack,int index) {
		while (stack > 0) {
			if (index == 0) {
				setFlagContent(DnvPConstants.FLAG_DATA_BEGIN);
			} else {
				setFlagContent(DnvPConstants.FLAG_DATA_EXTEND);
			}
			byte[] tempBuffer = new byte[DnvPConstants.MAX_RESPONSE_SIZE];
			System.arraycopy(bytes, index, tempBuffer, DnvPConstants.HEADER_RESPONSE,
					DnvPConstants.MAX_CONTENT_SIZE);
			System.arraycopy(bufferStream, 0, tempBuffer, 0, DnvPConstants.HEADER_RESPONSE);
			queueBuffer.add(tempBuffer);
			index += DnvPConstants.MAX_CONTENT_SIZE;
			stack -= 1;
		}
		if (index == 0) {
			setFlagContent(DnvPConstants.FLAG_DATA_CLONE);
		} else {
			setFlagContent(DnvPConstants.FLAG_DATA_END);
		}
		byte[] endBuffer = new byte[bytes.length - index + DnvPConstants.HEADER_RESPONSE];
		System.arraycopy(bytes, index, endBuffer, DnvPConstants.HEADER_RESPONSE, bytes.length - index);																							// hien																					// loi
		System.arraycopy(bufferStream, 0, endBuffer, 0, DnvPConstants.HEADER_RESPONSE);
		queueBuffer.add(endBuffer);
	}

	//// for write to output stream
	//// for: request have data or response
	protected void writeStream(OutputStream os) throws IOException {
		if (os != null) {
			synchronized (os) {
				load(serialize(hashMap));
				while (queueBuffer.isEmpty()) {
					byte[] streamByte = queueBuffer.poll();
					os.write(streamByte);
					try {
						Thread.sleep(timeDelay);
					} catch (InterruptedException e) {
						DnvPException.DnvPExceptionStatic("Interrupted exception - DnvP writeStream", e);
					}
				}
			}
		}
	}
	/// for request not data
	protected void writeInvokeFunc(OutputStream os) throws IOException {
		if(os != null) {
			synchronized (os) {
				os.write(bufferStream, 0, DnvPConstants.HEADER_REQUEST_NODATA);
			}
		}
	}
}