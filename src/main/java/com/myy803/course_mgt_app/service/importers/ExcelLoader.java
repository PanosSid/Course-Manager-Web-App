package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelLoader implements FileLoader {

	@Override
	public List<List<String>> getDataFromFile(MultipartFile file) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		List<List<String>> outerList = new ArrayList<>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			outerList.add(convertRowToListOfStr(row));
			
		}
		workbook.close();
		
		return outerList;
	}
	
	private List<String> convertRowToListOfStr(Row row) {
		List<String> rowStrList = new ArrayList<>();
		for (Cell cell : row) {
			rowStrList.add(getValueOfCellAsStr(cell));
		}
		System.out.println(rowStrList);
		return rowStrList;
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
        	return null;
//        	throw new Exception("Cell type is not numeric or string");
		}
	}
	
	
	/*
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
