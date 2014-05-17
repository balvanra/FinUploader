package asol.fin.uploader.writer;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import asol.fin.uploader.DataCell;
import asol.fin.uploader.Format;
import asol.fin.uploader.PresentationInfo;
import asol.fin.uploader.Table;

public class XLSXWriter implements Writer {

	String defaultSheetName = "Data";
	String defaultNumberFormat = "#,##0.00";

	int defaultBufferSize = 10000;

	String destPath;

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public XLSXWriter(String destPath) {
		this.setDestPath(destPath);
	}

	@Override
	public void write(Table table, PresentationInfo info) throws Exception {
		SXSSFWorkbook wb = new SXSSFWorkbook(defaultBufferSize);
		Sheet sheet = wb.createSheet();
		wb.setSheetName(0, defaultSheetName);

		int offsetRow = 1;
		int offsetCol = 0;

		fillSheet(sheet, table, offsetRow, offsetCol);

		for (int i = 0; i < table.getDimensionCols(); i++) {
			sheet.autoSizeColumn(i + offsetCol);
		}

		// TODO named range
		Name namedCel3 = wb.createName();
		namedCel3.setNameName("mojedata");
		String reference = defaultSheetName + "!$A$1:$D$5"; // area reference
		namedCel3.setRefersToFormula(reference);

		flushWorkbook(wb, info);
	}

	private void fillSheet(Sheet sheet, Table table, int rowoffset,
			int coloffset) {
		CellStyle styleNumber = sheet.getWorkbook().createCellStyle();
		styleNumber.setDataFormat((short) BuiltinFormats
				.getBuiltinFormat(defaultNumberFormat));
		styleNumber.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderLeft(XSSFCellStyle.BORDER_THIN);

		CellStyle styleString = sheet.getWorkbook().createCellStyle();
		styleString.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleString.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleString.setBorderRight(XSSFCellStyle.BORDER_THIN);
		styleString.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleString.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		styleString.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
				.getIndex());

		int rownum = 0 + rowoffset;
		int colnum = 0 + coloffset;
		Row row = sheet.createRow(rownum++);
		for (DataCell c : table) {
			if (colnum >= table.getDimensionCols() + coloffset) {
				row = sheet.createRow(rownum++);
				colnum = 0 + coloffset;
			}
			Cell cell = row.createCell(colnum++);
			if (c.getFormat().equals(Format.STRING)) {
				cell.setCellValue(c.getStrValue());
				cell.setCellStyle(styleString);
			} else if (c.getFormat().equals(Format.DOUBLE)) {
				cell.setCellValue(c.getNumValue().doubleValue());
				cell.setCellStyle(styleNumber);
			} else {
				throw new RuntimeException(String.format(
						"Unsupported format %s", c.getFormat()));
			}
		}
	}

	private void flushWorkbook(SXSSFWorkbook workbook, PresentationInfo info)
			throws Exception {
		FileOutputStream out = null;
		try {
			File dir = new File(getDestPath());
			dir.mkdirs();
			out = new FileOutputStream(new File(getDestPath() + File.separator
					+ info.getName() + ".xlsx"));

			workbook.write(out);
		} finally {
			if (out != null)
				out.close();
			if (workbook != null)
				workbook.dispose();
		}
	}
}
