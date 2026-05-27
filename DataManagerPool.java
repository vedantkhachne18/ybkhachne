package com.vstl.generic;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @ScriptName : DataManagerPool
 * @Description : Provides data from Excel for running tests with thread safety
 * @Author : Framework Developer
 */
public class DataManagerPool {

    private static ThreadLocal<XSSFWorkbook> threadLocalWorkbook = new ThreadLocal<>();
    private static ThreadLocal<XSSFSheet> threadLocalSheet = new ThreadLocal<>();
    private static ThreadLocal<FileInputStream> threadLocalFileInputStream = new ThreadLocal<>();
    private static ThreadLocal<Row> threadLocalHeaderRow = new ThreadLocal<>();
    private static ThreadLocal<Row> threadLocalTestDataRow = new ThreadLocal<>();
    
    public DataManagerPool() {}

    /**
     * @Method : loadTestData(String testDataFilePath)
     * @Description : Load Data from Excel for the running testCase and return as
     *               Object array (Thread-safe version)
     */
    public static Hashtable<String, Hashtable<String, String>> loadTestData(String testDataFilePath) {
        Hashtable<String, Hashtable<String, String>> objDataProvider = null;
        try {
            threadLocalFileInputStream.set(new FileInputStream(testDataFilePath));
            threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFileInputStream.get()));
            threadLocalSheet.set(threadLocalWorkbook.get().getSheetAt(0));
            
            threadLocalHeaderRow.set(threadLocalSheet.get().getRow(0));
            threadLocalTestDataRow.set(threadLocalSheet.get().getRow(1));

            int intLastRowNumber = threadLocalSheet.get().getLastRowNum();
            int intRowIndex = 0;
            String strBufferCell = "";
            String strTCID = "";
            objDataProvider = new Hashtable<String, Hashtable<String, String>>();

            while (intRowIndex <= intLastRowNumber) {
                strBufferCell = getCellValue(threadLocalSheet.get().getRow(intRowIndex), 0);
                if (strBufferCell.trim().equalsIgnoreCase("TC ID")) {
                    threadLocalHeaderRow.set(threadLocalSheet.get().getRow(intRowIndex));
                    threadLocalTestDataRow.set(threadLocalSheet.get().getRow(intRowIndex + 1));
                    strTCID = getCellValue(threadLocalTestDataRow.get(), 0);
                    Hashtable<String, String> dataValueSet = new Hashtable<String, String>();
                    int intColumnNo = 0;
                    // iterating over cells
                    do {
                        String strHeader = "", strTestData = "";
                        // Key Data
                        strHeader = getCellValue(threadLocalHeaderRow.get(), intColumnNo);
                        // Value
                        strTestData = getCellValue(threadLocalTestDataRow.get(), intColumnNo);
                        if (!strHeader.equals(""))
                            dataValueSet.put(strHeader, strTestData);
                        intColumnNo++;
                    } while (intColumnNo < threadLocalHeaderRow.get().getLastCellNum());

                    // put the dataValueSet hash-table in objDataProvider
                    objDataProvider.put(strTCID, dataValueSet);
                }
                intRowIndex++;
                intRowIndex++;
            }

            // Closing the input stream
            threadLocalFileInputStream.get().close();

        } catch (Exception exception) {
            System.out.println("I got exception : " + exception.getMessage());
        } finally {
            threadLocalFileInputStream.remove();
            threadLocalWorkbook.remove();
            threadLocalSheet.remove();
            threadLocalHeaderRow.remove();
            threadLocalTestDataRow.remove();
        }
        return objDataProvider;
    }

    /**
     * @Method : loadTestData(String testCaseID, String sheetName, String testDataFilePath)
     * @Description : Load Data from Excel for the running testCase and return as
     *               Object array (Thread-safe version)
     */
    public static Object[][] loadTestData(String testCaseID, String sheetName, String testDataFilePath) {
        ArrayList<Hashtable<String, String>> hashTableList = new ArrayList<Hashtable<String, String>>();
        Object[][] objDataProvider = null;
        try {
            threadLocalFileInputStream.set(new FileInputStream(testDataFilePath));
            threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFileInputStream.get()));
            threadLocalSheet.set(threadLocalWorkbook.get().getSheetAt(0));
            threadLocalHeaderRow.set(threadLocalSheet.get().getRow(0));
            threadLocalTestDataRow.set(threadLocalSheet.get().getRow(1));
            int lastRowNumber = threadLocalSheet.get().getLastRowNum();
            int rowIndex = 0;

            if (!sheetName.equals("")) {
                getColumnCount(sheetName);
            }

            while (rowIndex <= lastRowNumber) {
                String cellData = getCellValue(threadLocalSheet.get().getRow(rowIndex), 0);
                if (cellData.equalsIgnoreCase(testCaseID)) {
                    int headerRowCount = rowIndex - 1;
                    String bufferCell = getCellValue(threadLocalSheet.get().getRow(rowIndex), 0);
                    while (rowIndex <= lastRowNumber && !bufferCell.equalsIgnoreCase("TC ID")) {
                        if (bufferCell.equalsIgnoreCase(testCaseID)) {
                            threadLocalHeaderRow.set(threadLocalSheet.get().getRow(headerRowCount));
                            threadLocalTestDataRow.set(threadLocalSheet.get().getRow(rowIndex));
                            Hashtable<String, String> dataValueSet = new Hashtable<String, String>();
                            int clmNo = 0;
                            // iterating over cells
                            do {
                                String header = "", testData = "";
                                header = getCellValue(threadLocalHeaderRow.get(), clmNo);
                                testData = getCellValue(threadLocalTestDataRow.get(), clmNo);

                                if (!header.equals(""))
                                    dataValueSet.put(header, testData);
                                clmNo++;
                            } while (clmNo < threadLocalHeaderRow.get().getLastCellNum());

                            // put the hash-table in list
                            hashTableList.add(dataValueSet);
                            clmNo = 0;
                        }
                        rowIndex++;
                        if (rowIndex > lastRowNumber)
                            bufferCell = "";
                        else
                            bufferCell = getCellValue(threadLocalSheet.get().getRow(rowIndex), 0);
                    }
                    break;
                }
                rowIndex++;
            }

            objDataProvider = new Object[hashTableList.size()][2];
            int rowCount = 0;
            for (Hashtable<String, String> hashTable : hashTableList) {
                objDataProvider[rowCount][0] = "Run " + (rowCount + 1);
                objDataProvider[rowCount][1] = hashTable;
                rowCount++;
            }

            // Closing the input stream
            threadLocalFileInputStream.get().close();

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            hashTableList = null;
            threadLocalFileInputStream.remove();
            threadLocalWorkbook.remove();
            threadLocalSheet.remove();
            threadLocalHeaderRow.remove();
            threadLocalTestDataRow.remove();
        }
        return objDataProvider;
    }

    /**
     * @Method : getCellValue(Row testDataRow, int columnNumber)
     * @Description : Get Cell value for given cell (Used in loadDataProvider)
     */
    private static String getCellValue(Row testDataRow, int columnNumber) {
        if (testDataRow == null)
            return "";
        else {
            Cell testDataCell = testDataRow.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
            if (testDataCell == null)
                return "";
            else
                return testDataCell.toString().trim();
        }
    }

    public static boolean isSheetExist(String sheetName) {
        int index = threadLocalWorkbook.get().getSheetIndex(sheetName);
        return index != -1 || threadLocalWorkbook.get().getSheetIndex(sheetName.toUpperCase()) != -1;
    }

    public static int getColumnCount(String sheetName) {
        if (!isSheetExist(sheetName)) return -1;

        threadLocalSheet.set(threadLocalWorkbook.get().getSheet(sheetName));
        XSSFRow row = (XSSFRow) threadLocalSheet.get().getRow(0);

        if (row == null) return -1;

        return row.getLastCellNum();
    }
}
