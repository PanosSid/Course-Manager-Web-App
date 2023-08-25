package com.myy803.course_mgt_app.service.importers;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public class JSONLoader implements FileLoader {

	@Override
	public List<Map<String, String>> getDataFromFile(MultipartFile file) throws IOException {
	    String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
	    JSONArray jsonArray = new JSONArray(fileContent);
	    List<Map<String, String>> dataMaps = new ArrayList<>();
	    for (int i = 0; i < jsonArray.length(); i++) {
	        JSONObject jsonObject = jsonArray.getJSONObject(i);
	        Map<String, String> dataMap = createDataMap(jsonObject);
	        dataMaps.add(dataMap);
	    }
	    return dataMaps;
	}

	private Map<String, String> createDataMap(JSONObject jsonObject) {
	    Map<String, String> dataMap = new HashMap<>();
	    Iterator<String> keys = jsonObject.keys();
	    while (keys.hasNext()) {
	        String key = keys.next();
	        String value = jsonObject.get(key).toString();
	        dataMap.put(key, value);
	    }
	    return dataMap;
	}


}
