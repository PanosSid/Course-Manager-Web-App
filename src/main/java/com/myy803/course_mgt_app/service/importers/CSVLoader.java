package com.myy803.course_mgt_app.service.importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

public class CSVLoader implements FileLoader {

	private List<String> headerNames;

	@Override
	public List<Map<String, String>> getDataFromFile(MultipartFile file) throws IOException {
		List<CSVRecord> csvRecords = getCSVRecordsFromFile(file);
		return convertCSVRecordsToDataList(csvRecords);
	}

	private List<CSVRecord> getCSVRecordsFromFile(MultipartFile file) throws IOException {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
		CSVParser csvParser = new CSVParser(fileReader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
		headerNames = csvParser.getHeaderNames();
		List<CSVRecord> csvRecords = csvParser.getRecords();
		csvParser.close();
		return csvRecords;
	}

	private List<Map<String, String>> convertCSVRecordsToDataList(List<CSVRecord> csvRecords) {
		List<Map<String, String>> dataList = new ArrayList<>();
		for (CSVRecord csvRecord : csvRecords) {
			dataList.add(createDataMap(csvRecord));
		}
		return dataList;
	}

	private Map<String, String> createDataMap(CSVRecord csvRecord) {
		Map<String, String> dataMap = new HashMap<>();
		for (int i = 0; i < csvRecord.size(); i++) {
			dataMap.put(headerNames.get(i), csvRecord.get(i));
		}
		return dataMap;
	}
}
