package com.spordniar.nio.netty.file;

public class FileBurstInstruct {

	private Integer status;// 0 开始 1中间 2结尾 3完成
	private String clientFileUrl; //客户端文件url
	private Integer readPosition; //读取的位置
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getClientFileUrl() {
		return clientFileUrl;
	}
	public void setClientFileUrl(String clientFileUrl) {
		this.clientFileUrl = clientFileUrl;
	}
	public Integer getReadPosition() {
		return readPosition;
	}
	public void setReadPosition(Integer readPosition) {
		this.readPosition = readPosition;
	}
	
	
}
