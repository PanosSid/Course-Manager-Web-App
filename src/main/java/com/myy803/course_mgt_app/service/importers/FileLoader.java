package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileLoader {
	
	List<List<String>> getDataFromFile(MultipartFile file) throws IOException;
}
