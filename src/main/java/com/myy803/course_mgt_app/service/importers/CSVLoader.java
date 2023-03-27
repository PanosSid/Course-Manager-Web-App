package com.myy803.course_mgt_app.service.importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

public class CSVLoader implements FileLoader {

	@Override
	public List<List<String>> getDataFromFile(MultipartFile file) throws IOException{
		return convertRecordsToDataList(getRecordsFromFile(file));
	}
	
	private List<CSVRecord> getRecordsFromFile(MultipartFile file) throws IOException {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
		CSVParser csvParser = new CSVParser(fileReader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
		List<CSVRecord> csvRecords = csvParser.getRecords();
		csvParser.close();
		return csvRecords;
	}
	
	public List<List<String>> convertRecordsToDataList(List<CSVRecord> csvRecords) {
		List<List<String>> outerlist = new ArrayList<>();
		for (CSVRecord csvRecord : csvRecords) {
			List<String> innerList = new ArrayList<String>();
			for (int i = 0; i < csvRecord.size(); i++) {
				innerList.add(csvRecord.get(i));
			}
			outerlist.add(innerList);
		}
		return outerlist;
	}
}
