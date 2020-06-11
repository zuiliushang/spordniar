package com.spordniar.nio.netty.file01;

public class TransferBody {

	private String fileName; //文件名
	
	private Integer beginPos; //读取起始
	
	private Integer endPos; //读取结束
	
	private byte[] data; //数据

	private String fileUrl; //文件路径
	
	private String savePath; //存放路径
	
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
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
	
}
