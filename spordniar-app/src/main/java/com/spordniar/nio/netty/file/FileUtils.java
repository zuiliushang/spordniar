package com.spordniar.nio.netty.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils {

	public static FileBurstData readFile(String fileUrl, Integer readPosition) throws IOException {
		FileBurstData fileBurstData = new FileBurstData();
		File file = new File(fileUrl);
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(readPosition);
		byte[] buffer = new byte[1024 * 100];
		int readSize =  randomAccessFile.read(buffer);
		if (readSize < 0) {// over
			randomAccessFile.close();
			fileBurstData.setStatus(Constants.FileStatus.COMPLETE);
			return fileBurstData;
		}
		FileBurstData fileInfo = new FileBurstData();
		fileInfo.setFileUrl(fileUrl);
		fileInfo.setFileName(file.getName());
		fileInfo.setBeginPos(readPosition);
		fileInfo.setEndPos(readPosition + readSize);
		//不足1024需要拷贝去掉空字节
        if (readSize < 1024 * 100) {
            byte[] copy = new byte[readSize];
            System.arraycopy(buffer, 0, copy, 0, readSize);
            fileInfo.setData(copy);
            fileInfo.setStatus(Constants.FileStatus.END);
        } else {
            fileInfo.setData(buffer);
            fileInfo.setStatus(Constants.FileStatus.CENTER);
        }
        randomAccessFile.close();
        return fileInfo;
	}
	
	public static FileBurstInstruct writeFile(String baseUrl, FileBurstData fileBurstData) throws IOException {
		if (fileBurstData.getStatus().equals(Constants.FileStatus.COMPLETE)) {
			FileBurstInstruct fileBurstInstruct  = new FileBurstInstruct();
			fileBurstInstruct.setStatus(Constants.FileStatus.COMPLETE);
			return fileBurstInstruct;
		}
		File file = new File(baseUrl + "/" + fileBurstData.getFileName());
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		int begin = fileBurstData.getBeginPos();
		byte[] data = fileBurstData.getData();
		randomAccessFile.seek(begin);
		randomAccessFile.write(data);
		randomAccessFile.close();
		if (fileBurstData.getStatus().equals(Constants.FileStatus.END)) {
			FileBurstInstruct fileBurstInstruct = new FileBurstInstruct();
			fileBurstInstruct.setStatus(Constants.FileStatus.COMPLETE);
			return fileBurstInstruct;
		}
		FileBurstInstruct fileBurstInstruct = new FileBurstInstruct();
		fileBurstInstruct.setClientFileUrl(fileBurstData.getFileUrl());
		fileBurstInstruct.setReadPosition(fileBurstData.getEndPos()+1);
		fileBurstInstruct.setStatus(Constants.FileStatus.CENTER);
		return fileBurstInstruct;
	}
	
}
