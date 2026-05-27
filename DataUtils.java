package com.vstl.generic;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import org.testng.annotations.DataProvider;
import com.vstl.config.UtilConfig;
import com.vstl.driver.DriverFactory;

public class DataUtils {
	

	public DataUtils() {}
	
	public static Hashtable<String, Hashtable<String, String>> testDataTable = new Hashtable<String, Hashtable<String, String>>();
	public static  Hashtable<String, String> testDataForTest = new Hashtable<String, String>();

	/**
	 * @Method : loadDataProvider
	 * @param : testCaseID - test case id
	 * @param : testDataFile - test data file
	 * @Description : Load Data from Excel for the running testCase and return as
	 *              Object array
	 * @author : Framework Developer
	 */
	public static void loadDataProvider(String testDataFilePath) {
		testDataTable = DataManagerPool.loadTestData(testDataFilePath);
	}

	/**
	 * @Method : loadTestData
	 * @param : runID - test case run id
	 * @param : dataSet - test data hash table
	 * @Description : Load data from excel for the running testCase and return as
	 *              Object array
	 * @author : Framework Developer
	 */
	public static void loadTestData(String TCIDRowNumber) {
		testDataForTest = testDataTable.get(TCIDRowNumber);
		DriverFactory.getObjAssertLogUtils().logReporter("testDataForTest------->" + testDataForTest,true);
		DriverFactory.setDataPoolHashTable(testDataForTest);
		DriverFactory.getObjAssertLogUtils().logReporter("TCID " + TCIDRowNumber + " Startred at "
				+ getDateInSpecifiedFormat("dd-MMM-yyyy-HH-mm-ss"),true);
		System.out.println("TCID " + TCIDRowNumber + " Startred at "
				+ getDateInSpecifiedFormat("dd-MMM-yyyy-HH-mm-ss"));
		DriverFactory.setCurrentTCID(TCIDRowNumber);
	}

	// For DataProvider
	public static void loadTestData(String runID, Hashtable<String, String> dataSet) {
		DriverFactory.setRunID(runID);
		DriverFactory.setDataPoolHashTable(dataSet);
		DriverFactory.getLogger().info("TEST DATA FOR TCID ----> " + dataSet.toString());
		System.out.println("TEST DATA FOR TCID ----> " + dataSet.toString());
		// DriverManager.getObjAssertLogUtils().logReporter("TEST DATA FOR TCID ----> "
		// + dataSet.toString(), true);
	}

	public static Object[][] loadDataProvider(String TCID, String testDataFilePath) {
		Object[][] dataPool = null;
		System.out.println("TCID : " + TCID + " And testDataFile :  " + testDataFilePath);
		DriverFactory.setTestCaseID(TCID);
		dataPool = DataManagerPool.loadTestData(TCID, "", UtilConfig.TCID_XL_PATH);
		return dataPool;
	}

	public static Object[][] loadDataProvider(String TCID, String sheetName, String testDataFilePath) {
		Object[][] dataPool = null;
		System.out.println("TCID : " + TCID + " And testDataFile :  " + testDataFilePath);
		DriverFactory.setTestCaseID(TCID);
		dataPool = DataManagerPool.loadTestData(TCID, sheetName, UtilConfig.TCID_XL_PATH);
		return dataPool;
	}

	public static Object[][] loadDataProvider(String TCID, String sheetName, String module, String testDataFilePath) {
		Object[][] dataPool = null;
		System.out.println("TCID : " + TCID + " And testDataFile :  " + testDataFilePath);
		DriverFactory.setTestCaseID(TCID);
		dataPool = DataManagerPool.loadTestData(TCID, sheetName, UtilConfig.TCID_XL_PATH);
		return dataPool;
	}

	/**
	 * Description : This method is used for to return array object..
	 * 
	 * @author : Automation Developer
	 */
	@DataProvider(name = "TestDataProvider", parallel = true)
	public static Object[][] getDataProvider(Method objMethod) {
		String TCID = objMethod.getName();
	//	System.out.println("get TestData File Path : " + DriverManager.getTestDataFilePath());
		Object[][] testData = loadDataProvider(TCID, TCID, UtilConfig.TCID_XL_PATH);
		return testData;
	}

	/**
	 * @Method: dpString
	 * @Description: this method returns data from the the previously loaded
	 *               datapool
	 * @param columnHeader - excel file header column name
	 * @return - value for corresponding header
	 * @author Automation Tester
	 * @CreationDate: 27 April 2015
	 * @ModifiedDate:
	 */
	public static String dpString(String columnHeader) {
		Hashtable<String, String> dataPoolHashTable = DriverFactory.getDataPoolHashTable();
		try {
			if (dataPoolHashTable.get(columnHeader) == null)
				return "";
			else {
				// System.out.println("I found, Key: " + columnHeader + " Value : " +
				// dataPoolHashTable.get(columnHeader));
				return dataPoolHashTable.get(columnHeader);
			}
		} catch (Exception exception) {
			System.out.println("Developer Side Issue");
			throw new RuntimeException(exception);
		}
	}

	/*
	 * @Method : getDateInSpecifiedFormat
	 * 
	 * @Description : This method takes parameter of your required DateFormat Type
	 * Like: dd-mm-YYYY DD.MM.YYYY and in return it will give you today's date in
	 * specified date format
	 * 
	 * @param : dateFormat like : dd-MM-YYYY
	 * 
	 * @author : Framework Developer
	 * 
	 */
	public static String getDateInSpecifiedFormat(String dateFormat) {
		String current_date = "";
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		current_date = formatter.format(today);
		// System.out.println("getDateInSpecifiedFormat "+dateFormat + " -
		// "+current_date);
		return current_date;
	}
	
}
