package com.buzzai.dnvp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

class DnvPTransactionCore {
	
	/**
	 * This here, send message to client/server
	 */
	private Socket transSocket = null;
	private Queue<DnvP> queueRequest = new ArrayDeque<DnvP>();
	private Vector<DnvP> vectorResponse = new Vector<DnvP>();
	private Thread threadInput = null;
	private short timedelay = 10; // for wait response
	private int awaittimeout = 4000; // milliseconds for a request
	// total time = 1 minute - for 1MB
	protected String strPackage = null;

	protected DnvPTransactionCore() {}
	
	protected DnvPTransactionCore(Socket sk) {
		this.transSocket = sk;
		DnvPInputStream input = new DnvPInputStream(this);
		threadInput = new Thread(input);
		threadInput.start();
	}
	
	public synchronized Queue<DnvP> getQueueRequest() {
		return this.queueRequest;
	}
	
	protected void addRequest(DnvP dnvp) {
		this.queueRequest.add(dnvp);
	}
	
	protected void addResponse(DnvP dnvp) {
		this.vectorResponse.addElement(dnvp);
	}
	
	protected synchronized InputStream getInputStream() throws IOException {
		return this.transSocket.getInputStream();
	}

	protected DnvP sendRequest(DnvP dnvp) {
		dnvp.setTypePacket(DnvPConstants.FLAG_PACKET_REQUEST);
		DnvP packetResp = null;
		if (isOpen()) {
			try {
				if (dnvp.flagPacket == DnvPConstants.FLAG_PACKET_DATA) {
					dnvp.writeStream(transSocket.getOutputStream());
				} else {
					dnvp.writeInvokeFunc(transSocket.getOutputStream());
				}
			} catch(IOException e) {
				DnvPException.DnvPExceptionStatic("IO Exception - DnvPTransaction SendRequest", e);
			}
			int timeIdx = 0;
			try {
				while (isOpen() && timeIdx < awaittimeout) {
					Thread.sleep(timedelay);
					packetResp = getResponse(dnvp.idPacket); 
					if (packetResp != null) {
						break;
					}
					++timeIdx;
				}
			} catch (InterruptedException e) {
				DnvPException.DnvPExceptionStatic("Interupted - DnvPTransaction", e);
			}
			
		}
		return packetResp;
	}

	protected void pushPacket(DnvP dnvp) {
		dnvp.setTypePacket(DnvPConstants.FLAG_PACKET_REQUEST);
		if (isOpen()) {
			try {
				dnvp.writeStream(transSocket.getOutputStream());
			} catch (IOException e) {
				DnvPException.DnvPExceptionStatic("Push Packet in DnvPTransaction", e);
			}
		}
	}
	
	protected DnvP getResponse(long idRes) {
		for(int i = 0 ; i < vectorResponse.size(); ++i) {
			if(vectorResponse.elementAt(i).idPacket == idRes) {
				return vectorResponse.elementAt(i);
			}
		}
		return null;
	}

	protected void send(DnvP dnvp) {
		if (isOpen()) {
			try {
				dnvp.writeStream(transSocket.getOutputStream());
			} catch(IOException e) {
				DnvPException.DnvPExceptionStatic("Send dnvp packet in DnvPTransaction", e);
			}		
		}
	}

	public boolean isOpen() {
		if (transSocket != null && !transSocket.isClosed()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void closeTransaction() {
		if(isOpen()) {
			threadInput.stop();
		}
	}
}
