package com.myy803.course_mgt_app.unit.service.importers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.myy803.course_mgt_app.service.importers.ExcelLoader;

public class ExcelLoaderUnit {

	private ExcelLoader excelLoader = new ExcelLoader();

	@Test
	void testGetDataFromFile() throws IOException {
		String[][] datatypes = { { "CourseId", "Name", "InstructorLogin", "Semester", "Year", "Syllabus" },
				{ "MYY-301", "Software Development I", "panos", "Spring", "3", "Software development basics" },
				{ "PLH-010", "Advanced Databases", "panos", "Fall", "4", "Advanced DB and more" } };
		
		byte[] bytes = createByteArray(datatypes);

		MockMultipartFile file = new MockMultipartFile("test.xlsx", "test.xlsx", "", bytes);

		List<Map<String, String>> expected = new ArrayList<>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("CourseId", "MYY-301");
		map1.put("Name", "Software Development I");
		map1.put("InstructorLogin", "panos");
		map1.put("Semester", "Spring");
		map1.put("Year", "3");
		map1.put("Syllabus", "Software development basics");
		expected.add(map1);
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("CourseId", "PLH-010");
		map2.put("Name", "Advanced Databases");
		map2.put("InstructorLogin", "panos");
		map2.put("Semester", "Fall");
		map2.put("Year", "4");
		map2.put("Syllabus", "Advanced DB and more");
		expected.add(map2);

		Assertions.assertEquals(expected, excelLoader.getDataFromFile(file));
	}

	// create byte array to use with MockMultipartFile
	// --------------------------------
	public byte[] createByteArray(Object[][] datatypes) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("test");
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
			workbook.write(bos);
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

}
