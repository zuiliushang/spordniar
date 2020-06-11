package com.spordniar.nio.netty.file;

public class FileBurstData {

	private String fileUrl; //客户端文件地址
	private String fileName; // 文件名
	private Integer beginPos;
	private Integer endPos;
	private byte[] data;
	private Integer status;//
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getBeginPos() {
		return beginPos;
	}
	public void setBeginPos(Integer beginPos) {
		this.beginPos = beginPos;
	}
	public Integer getEndPos() {
		return endPos;
	}
	public void setEndPos(Integer endPos) {
		this.endPos = endPos;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
