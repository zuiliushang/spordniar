package com.spordniar.nio.netty.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileCacheUtils {

	// {filename: FileBurstInstruct}
	public static Map<String, FileBurstInstruct> burstInstructCacheMap = new ConcurrentHashMap<String, FileBurstInstruct>();
	
}
