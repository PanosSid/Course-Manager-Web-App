package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelLoader implements FileLoader {
	
	private List<String> headerNames;

	@Override
	public List<Map<String, String>> getDataFromFile(MultipartFile file) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		headerNames = getHeaderNamesFromSheet(sheet);
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			list.add(convertRowToListOfStr(row));
		}
		workbook.close();
		return list;
	}
	
	private List<String> getHeaderNamesFromSheet(Sheet sheet){
		List<String> headerNames = new ArrayList<String>();
		Row header = sheet.getRow(0);
		for (Cell cell: header) {
			headerNames.add(cell.getStringCellValue());
		}
		return headerNames;
	}
	
	private Map<String, String> convertRowToListOfStr(Row row) {
		Map<String, String> dataMap = new HashMap<>();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			dataMap.put(headerNames.get(i), getValueOfCellAsStr(row.getCell(i)));
		}
		return dataMap;
	}

	private String getValueOfCellAsStr(Cell cell){
		switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING: 
        	return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC: 
        	return removeRedundantDecimals(cell.getNumericCellValue());        	
        case Cell.CELL_TYPE_BLANK: 
        	return "";  
        default:
        	throw new RuntimeException("Cell type is not numeric or string");
		}
	}
	
	
	/**
	 * Used because cell.getNumericCellValue() returns every number, including integers,
	 * as a decimal (etx 3 -> 3.0), which can cause issues when converting decimals
	 * to integers in the importers.
	 */
	private String removeRedundantDecimals(double primD) {
		Double d = Double.valueOf(primD);
		if (d == d.intValue()) {
			return d.intValue() + "";
		}
		return d.toString();
	}

}
