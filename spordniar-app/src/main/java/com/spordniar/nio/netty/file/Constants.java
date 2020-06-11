package com.spordniar.nio.netty.file;

public class Constants {

	public static class FileStatus{
		public static int BEGIN = 0;//开始
		public static int CENTER = 1; //中间
		public static int END = 2; // 结束
		public static int COMPLETE = 3; //完成
	}
	
	public static class TransferType{
		public static int REQUEST = 0;//请求
		public static int INSTRUCT = 1; // 传输
		public static int DATA = 2; //数据
	}
	
}
