/**
 * @Copyright 2008 版权归陈仁飞，不要肆意侵权抄袭，如引用请注明出处保留作者信息。
 */
package com.sagframe.sqltoy.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sagacity.sqltoy.utils.FileUtil;
import org.sagacity.sqltoy.utils.IOUtil;
import org.sagacity.sqltoy.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @project sagacity-core
 * @description 读写Excel文件的工具类,支持xls和xlsx格式
 * @author chenrenfei <a href="mailto:zhongxuchen@hotmail.com">联系作者</a>
 * @version id:ExcelUtil.java,Revision:v1.0,Date:2008-12-14 下午10:02:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExcelUtil {
	/**
	 * 定义日志
	 */
	private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	private final static String XLSX_SUFFIX = ".xlsx";

	public static void writer(List rowDatas, String distFile, String sheetName) throws Exception {
		writer(rowDatas, distFile, null, sheetName);
	}

	public static void writer(List rowDatas, String distFile, String blobSaveFile, String sheetName) throws Exception {
		writer(rowDatas, distFile, blobSaveFile, sheetName, null);
	}

	/**
	 * @todo <b>数据行导出</b>
	 * @param rowDatas
	 * @param distFile
	 * @param blobSaveFile
	 * @param sheetName
	 * @param charset
	 * @throws Exception
	 */
	public static void writer(List rowDatas, String distFile, String blobSaveFile, String sheetName, String charset)
			throws Exception {
		if (rowDatas == null) {
			logger.info(distFile + "对应的表或查询没有数据!");
			return;
		}
		Workbook wb = null;
		Sheet xlsSheet = null;
		try {
			boolean isXls = true;
			if (distFile.trim().toLowerCase().endsWith(XLSX_SUFFIX)) {
				isXls = false;
			}
			wb = isXls ? new HSSFWorkbook() : new XSSFWorkbook();
			xlsSheet = wb.createSheet(sheetName == null ? "sheet1" : sheetName);
			// 标题样式
			CellStyle headStyle = wb.createCellStyle();
			headStyle.setBorderTop(BorderStyle.THIN);
			headStyle.setBorderLeft(BorderStyle.THIN);
			headStyle.setBorderBottom(BorderStyle.THIN);
			headStyle.setBorderRight(BorderStyle.THIN);
			headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headStyle.setAlignment(HorizontalAlignment.CENTER);
			headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			// 表头字体
			Font headFont = wb.createFont();
			headFont.setBold(true);
			headStyle.setFont(headFont);

			CellStyle valueStyle = wb.createCellStyle();
			valueStyle.setBorderTop(BorderStyle.THIN);
			valueStyle.setBorderBottom(BorderStyle.THIN);
			valueStyle.setBorderLeft(BorderStyle.THIN);
			valueStyle.setBorderRight(BorderStyle.THIN);

			// 取第一行
			setTitle(xlsSheet, headStyle, (List) rowDatas.get(0));
			// 删除第一行
			rowDatas.remove(0);
			if (!rowDatas.isEmpty()) {
				addRows(xlsSheet, valueStyle, rowDatas, ((List) rowDatas.get(0)).size(), blobSaveFile, charset);
			}
			File exportFile = new File(distFile);
			// 判断路径是否存在，不存在则创建路径
			if (!exportFile.getParentFile().exists()) {
				exportFile.getParentFile().mkdirs();
			}
			FileOutputStream fileOut = new FileOutputStream(exportFile);
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(distFile + "导出失败!");
			throw e;
		} finally {
			xlsSheet = null;
			if (wb != null) {
				wb.close();
				wb = null;
			}
		}
	}

	/**
	 * @todo 设置标题
	 * @param xlsSheet
	 * @param titleStyle
	 * @param titles
	 */
	private static void setTitle(Sheet xlsSheet, CellStyle titleStyle, List titles) {
		Row title = xlsSheet.createRow(0);
		Object colCell;
		for (int var = 0, n = titles.size(); var < n; var++) {
			colCell = titles.get(var);
			Cell cell = title.createCell(var);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(colCell == null ? "" : colCell.toString());
		}
	}

	/**
	 * @todo <b>写入行数据</b>
	 * @param xlsSheet
	 * @param valueStyle
	 * @param rowsData
	 * @param width
	 * @param saveBlobFile
	 * @param charset
	 * @throws Exception
	 */
	private static void addRows(Sheet xlsSheet, CellStyle valueStyle, List rowsData, int width, String saveBlobFile,
			String charset) throws Exception {
		if (rowsData == null || rowsData.isEmpty()) {
			return;
		}
		List row;
		Object cellData;
		Cell cell;
		String fieldType;
		String fileName;
		Row rowDatas;
		Blob tmpBlob;
		for (int i = 0, n = rowsData.size(); i < n; i++) {
			rowDatas = xlsSheet.createRow(i + 1);
			row = (List) rowsData.get(i);
			if (width > row.size()) {
				width = row.size();
			}
			for (int j = 0; j < width; j++) {
				cell = rowDatas.createCell(j);
				cell.setCellStyle(valueStyle);
				cellData = row.get(j);
				if (cellData == null) {
					cell.setCellValue("");
				} else if (cellData instanceof Boolean) {
					cell.setCellValue(((Boolean) cellData).booleanValue());
				} else if (cellData instanceof Number) {
					cell.setCellValue(cellData.toString());
				} else {
					if (StringUtil.isNotBlank(saveBlobFile)) {
						fieldType = row.get(j).getClass().getName();
						fileName = FileUtil.linkPath(saveBlobFile, System.nanoTime() + ".tmp");
						if (StringUtil.indexOfIgnoreCase(fieldType, "blob") != -1) {
							tmpBlob = (java.sql.Blob) row.get(j);
							FileUtil.putBytesToFile(tmpBlob.getBytes(1, (int) tmpBlob.length()), fileName);
							cell.setCellValue(fileName);
						} else if (StringUtil.indexOfIgnoreCase(fieldType, "clob") != -1) {
							FileUtil.putStrToFile(clobToString((java.sql.Clob) row.get(j)), fileName, charset);
							cell.setCellValue(fileName);
						} else if (StringUtil.indexOfIgnoreCase(fieldType, "image") != -1) {
							FileUtil.putBytesToFile(row.get(j).toString().getBytes(), fileName);
							cell.setCellValue(fileName);
						} else {
							cell.setCellValue(row.get(j) == null ? "" : row.get(j).toString());
						}
					} else {
						if (row.get(j) == null) {
							cell.setCellValue("");
						} else if (row.get(j) instanceof java.sql.Clob) {
							cell.setCellValue(clobToString((Clob) row.get(j)));
						} else {
							cell.setCellValue(row.get(j).toString());
						}
					}
				}
			}
		}
	}

	/**
	 * @todo <b>读取excel文件</b>
	 * @param excelData
	 * @param beginRow
	 * @param endRow
	 * @param beginCol
	 * @param endCol
	 * @return
	 * @throws Exception
	 */
	public static List read(Object excelData, Integer beginRow, Integer endRow, Integer beginCol, Integer endCol)
			throws Exception {
		// 默认xlsx格式
		return read("xlsx", excelData, null, beginRow, endRow, beginCol, endCol);
	}

	public static List read(Object excelData, String sheetName, Integer beginRow, Integer endRow, Integer beginCol,
			Integer endCol) throws Exception {
		// 默认xlsx格式
		return read("xlsx", excelData, sheetName, beginRow, endRow, beginCol, endCol);
	}

	/**
	 * @todo <b>读取指定sheet的excel文件</b>
	 * @param excelData
	 * @param sheetName
	 * @param beginRow
	 * @param endRow
	 * @param beginCol
	 * @param endCol
	 * @return
	 * @throws Exception
	 */
	public static List read(String excelFileSuffix, Object excelData, String sheetName, Integer beginRow,
			Integer endRow, Integer beginCol, Integer endCol) throws Exception {
		if (excelData == null) {
			return null;
		}
		InputStream excelInputStream = null;
		boolean isXls = excelFileSuffix.equalsIgnoreCase("xls") ? true : false;
		if (excelData instanceof InputStream) {
			excelInputStream = (InputStream) excelData;
		} else if (excelData instanceof byte[]) {
			excelInputStream = new ByteArrayInputStream((byte[]) excelData);
		} else if (excelData instanceof File) {
			excelInputStream = new FileInputStream((File) excelData);
			if (((File) excelData).getName().toLowerCase().endsWith(XLSX_SUFFIX)) {
				isXls = false;
			}
		} else if (excelData instanceof String) {
			String fileName = ((String) excelData).trim();
			if (fileName.toLowerCase().endsWith(XLSX_SUFFIX)) {
				isXls = false;
			}
			File excelFile = new File(fileName);
			if (excelFile.exists()) {
				excelInputStream = new FileInputStream(excelFile);
			} else {
				if (StringUtil.indexOfIgnoreCase(fileName, "classpath:") == 0) {
					fileName = fileName.substring(10).trim();
				}
				if (fileName.charAt(0) == '/') {
					fileName = fileName.substring(1);
				}
				excelInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
				if (excelInputStream == null) {
					try {
						Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(fileName);
						URL url;
						while (urls.hasMoreElements()) {
							url = urls.nextElement();
							excelInputStream = new FileInputStream(url.getFile());
							if (excelInputStream != null) {
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (excelInputStream == null) {
			return null;
		}

		Workbook wb = null;
		Sheet sheet;
		List result = new ArrayList();
		try {
			wb = isXls ? new HSSFWorkbook(excelInputStream) : new XSSFWorkbook(excelInputStream);
			if (StringUtil.isNotBlank(sheetName)) {
				sheet = wb.getSheet(sheetName);
			} else {
				sheet = wb.getSheetAt(0);
			}
			// excel数据为空
			if (sheet.getLastRowNum() < 1) {
				wb.close();
				return null;
			}
			// 是否存在合并
			boolean hasMerge = hasMerged(sheet);
			// 开始行
			int realBeginRow = (beginRow == null) ? 1 : beginRow.intValue() - 1;
			// 结束行
			int realEndRow = (endRow == null || endRow < 0) ? sheet.getLastRowNum() : endRow.intValue() - 1;
			// 开始列
			int realBeginCol = (beginCol == null) ? 0 : beginCol.intValue() - 1;
			// 结束列
			int realEndCol;
			Object content = null;
			Row row;
			int emptyCnt = 0;
			// 读取excel数据内容
			Cell cell;
			while (realEndRow >= realBeginRow) {
				row = sheet.getRow(realBeginRow);
				if (row != null) {
					List rowData = new ArrayList();
					emptyCnt = 0;
					realEndCol = (endCol == null || endCol < 0)
							? (row.getLastCellNum() > 255 ? 255 : row.getLastCellNum() - 1)
							: endCol.intValue() - 1;
					for (int i = realBeginCol; i <= realEndCol; i++) {
						if (i >= row.getLastCellNum()) {
							content = null;
						} else {
							cell = row.getCell(i);
							if (cell == null) {
								content = null;
							} else {
								// 存在合并区域
								if (hasMerge) {
									content = getMergedRegionValue(sheet, cell, row.getRowNum(), cell.getColumnIndex());
								} else {
									content = getCellValue(cell);
								}
							}
						}
						rowData.add(content);
						if (content == null || content.toString().equals("")) {
							emptyCnt++;
						}
					}
					if (emptyCnt == realEndCol - realBeginCol + 1 && emptyCnt != 0) {
						break;
					}
					result.add(rowData);
				}
				realBeginRow++;
			}
			excelInputStream.close();
		} catch (Exception e) {
			throw e;
		} finally {
			sheet = null;
			if (wb != null) {
				wb.close();
				wb = null;
			}
		}
		return result;
	}

	/**
	 * @todo 获取合并单元格的值
	 * @param sheet
	 * @param cell
	 * @param row
	 * @return
	 */
	private static Object getMergedRegionValue(Sheet sheet, Cell cell, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					// 取合并区域的第一行第一列的值
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getCellValue(fCell);
				}
			}
		}
		return getCellValue(cell);
	}

	/**
	 * @todo 获取单元格的值
	 * @param cell
	 * @return
	 */
	private static Object getCellValue(Cell cell) {
		Object content = null;
		if (null == cell) {
			content = null;
		} else {
			switch (cell.getCellType()) {
			case STRING:
				content = cell.getStringCellValue();
				break;
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					content = cell.getDateCellValue();
				} else {
					// 避免科学计数法
					content = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
					// 避免数字最后多出一个.0
					String tmp = content.toString();
					if (tmp.endsWith(".0")) {
						content = tmp.substring(0, tmp.length() - 2);
					}
				}
				break;
			case BOOLEAN:
				content = cell.getBooleanCellValue();
				break;
			case FORMULA:
				content = cell.getCellFormula();
				break;
			case BLANK:
				content = "";
				break;
			case _NONE:
				content = null;
				break;
			default:
				content = null;
			}
		}
		return content;
	}

	/**
	 * @todo clob转换成字符串
	 * @param clob
	 * @return
	 */
	private static String clobToString(Clob clob) {
		if (clob == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(1024 * 8);// 8K
		Reader clobStream = null;
		try {
			clobStream = clob.getCharacterStream();
			char[] b = new char[1024];// 每次获取1K
			int i = 0;
			while ((i = clobStream.read(b)) != -1) {
				sb.append(b, 0, i);
			}
		} catch (Exception ex) {
			sb = null;
		} finally {
			IOUtil.closeQuietly(clobStream);
		}
		if (sb == null) {
			return null;
		} else {
			return sb.toString();
		}
	}

	/**
	 * @todo 判断sheet页中是否含有合并单元格
	 * @param sheet
	 * @return
	 */
	private static boolean hasMerged(Sheet sheet) {
		return sheet.getNumMergedRegions() > 0 ? true : false;
	}
}
