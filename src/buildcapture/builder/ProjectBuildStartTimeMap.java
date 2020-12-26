/*
 * Copyright(c) 2020 NEXCO Systems company limited All rights reserved.
 */
package buildcapture.builder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public final class ProjectBuildStartTimeMap {
	private static final Map<String, LocalDateTime> map = new HashMap<>();
	
	public static void put(String project, LocalDateTime localDateTime) {
		map.put(project, localDateTime);
	}
	
	public static LocalDateTime get(String project) {
		return map.get(project);
	}
}

