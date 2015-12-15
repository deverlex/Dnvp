package com.buzzai.dnvp;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

final class DnvPConstants {

	//// 1 byte for type of message
	public static final byte FLAG_PACKET_REQUEST = -1;
	public static final byte FLAG_PACKET_RESPONSE = 1;
	// 1 byte for second flag
	public static final byte FLAG_PACKET_NO_DATA = -1;
	public static final byte FLAG_PACKET_DATA = 1;
	/// 1 byte for  flag
	public static final byte FLAG_DATA_CLONE = -1;
	public static final byte FLAG_DATA_BEGIN = 1;
	public static final byte FLAG_DATA_EXTEND = 2;
	public static final byte FLAG_DATA_END = 3;

	/// ||type msg||id||flag||package||class||func||flagContent||lengContent||Content||
	/// ----------1---9-----10-------60------90----120----------121---------125-------
	/// ||type msg||id||flagContent||lengContent||Content||
	/// ----------1---9------------10-----------14--------
	public static final short LENGTH_TYPE_PACKET = 1;
	public static final short LENGTH_ID_PACKET = 8;
	public static final short LENGTH_FLAG_DATA = 1; // No data or data
	public static final short LENGTH_NAME_PACKAGE  = 50; //leng of package
	public static final short LENGTH_NAME_CLASS = 30; // leng of class
	public static final short LENGTH_NAME_FUNCTION = 30; // leng of method
	public static final short LENGTH_FLAG_CONTENT = 1; // begin || extend || end
	public static final short LENGTH_CONTENT = 4;
	
	public static final short INDEX_TYPE_PACKET = 0;
	public static final short INDEX_TYPE_ID_PACKET = 1;
	// request
	public static final short INDEX_FLAG_DATA_REQUEST = 9;
	public static final short INDEX_PACKAGE_REQUEST = 10;
	public static final short INDEX_CLASS_REQUEST = 60;
	public static final short INDEX_FUNCTION_REQUEST = 90;
	public static final short INDEX_FLAG_CONTENT_REQUEST = 120;
	public static final short INDEX_LENGTH_CONTENT_REQUEST = 121;
	public static final short INDEX_CONTENT_REQUEST = 125;
	// response
	public static final short INDEX_FLAG_CONTENT_RESPONSE = 9;
	public static final short INDEX_LENGTH_CONTENT_RESPONSE = 10;
	public static final short INDEX_CONTENT_RESPONSE = 14;
	
	public static final short HEADER_REQUEST = 125; // leng of header
	public static final short HEADER_RESPONSE = 14;
	public static final short HEADER_REQUEST_NODATA = 120;
	
	public static final int MAX_REQUEST_SIZE = 8317; // max size for file is 8KB
												// + 125B Header
	public static final int MAX_RESPONSE_SIZE = 8206; //max size for file is 8KB + 14B header
	public static final int MAX_CONTENT_SIZE = 8192; // 8KB 
	public static final int MAX_MESSAGE_SIZE = 1048576; // max size is 1MB not have HEADER
}
