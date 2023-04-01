package com.myy803.course_mgt_app.unit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.myy803.course_mgt_app.service.importers.ExcelLoader;

public class UnitTestExcelLoader {
	MockMultipartFile file;
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("test");


	// create byte array to use with MockMultipartFile
	// --------------------------------
	public byte[] createByteArray(Object[][] datatypes, XSSFWorkbook workBook, XSSFSheet sheet) {
		int rowNum = 0;

		for (Object[] datatype : datatypes) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : datatype) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
//			workBook.write(bos);
			workBook.write(bos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] bytes = bos.toByteArray();
		return bytes;
	}

	@Test
	void testGetDataFromFile() throws IOException {
		String[][] datatypes = {
				{ "Id", "Name", "InstructorLogin", "Semester", "Year", "Syllabus" },
				{ "MYY-301", "Software Development I", "pvassil", "1", "3", "Software development basics" },
				{ "PLH-010", "Advanced Databases", "pvassil", "2", "4", "Advanced DB and more" } };

		byte[] bytes = createByteArray(datatypes, workbook, sheet);

		file = new MockMultipartFile("test.xlsx", "test.xlsx", "", bytes);

		ExcelLoader excelLoader = new ExcelLoader();
		
		List<List<String>> actualList = excelLoader.getDataFromFile(file);
		
		List<List<String>> expectedList = new ArrayList<>();
		expectedList.add(arrayToList(datatypes[1]));
		expectedList.add(arrayToList(datatypes[2]));
		
		Assertions.assertEquals(expectedList, actualList);
		
	}
	
	private List<String> arrayToList(String[] arr) {
		List<String> outerList = new ArrayList<String>();
		for (int i = 0; i < arr.length; i++) {
			outerList.add(arr[i]);
		}
		return outerList;
	}
}
