package com.spordniar.nio.netty.file;

public class FileTransferProtocol {

	private Integer transferType; // 0请求传输列表，1文件传输指令 2文件传输数据
	
	private Object transferObj; //数据对象： 0FileDescInfo  FileBurstInstruct FileBurstData

	public Integer getTransferType() {
		return transferType;
	}

	public void setTransferType(Integer transferType) {
		this.transferType = transferType;
	}

	public Object getTransferObj() {
		return transferObj;
	}

	public void setTransferObj(Object transferObj) {
		this.transferObj = transferObj;
	}
	
}
