package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface FileLoader {
	
	List<Map<String, String>> getDataFromFile(MultipartFile file) throws IOException;
	
}
