package com.vstl.scripts.ui.openMRS;

import static com.vstl.config.ConfigurationManager.configuration;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Hashtable;
import org.testng.SkipException;
import org.testng.annotations.Test;
import com.vstl.data.dynamic.DynamicDataFactory;
import com.vstl.driver.DriverFactory;
import com.vstl.flows.openMRS.LoginLogoutFlow;
import com.vstl.flows.openMRS.RegisterPatientFlow;
import com.vstl.generic.DataUtils;
import com.vstl.generic.TestBase;
import io.qameta.allure.Description;

public class CreatePatientTest extends TestBase{
	
	private LoginLogoutFlow objLoginLogoutFlow;
	private RegisterPatientFlow objRegisterPatientFlow;
	private boolean assertType = false;

	public void initializePagesAndFlows() {
		assertType = configuration().getAssertType();
		objRegisterPatientFlow = new RegisterPatientFlow();
		objLoginLogoutFlow = new LoginLogoutFlow();
	}

	@Description("VERIFY CREATE DYNAMIC PATIENT IN TO THE APPLICATION")
	@Test(dataProviderClass = DataUtils.class, dataProvider = "TestDataProvider")
	public void TCID_112_CreateDynamicPatient(Method method, String strRun,
			Hashtable<String, String> dataSetValue) {
		DataUtils.loadTestData(strRun, dataSetValue);
		DriverFactory.setRunID(strRun);
		DriverFactory.setTestCaseID(DataUtils.dpString("TC ID"));
		Date date = new Date();
		DriverFactory.getObjAssertLogUtils().logReporter("TEST CASE EXECUTION STARTED FOR : " + method.getName() + " " + date,true);
		//System.out.println("TEST CASE EXECUTION STARTED FOR : " + method.getName() + " " + date);
		if (!DataUtils.dpString("runmode").equals("Y")) {
			throw new SkipException("Run Mode 'No'");
		}
		this.initializePagesAndFlows();
		objLoginLogoutFlow.doLogin(DataUtils.dpString("userID"), DataUtils.dpString("password"));
		objLoginLogoutFlow.verifyLoginSuccessfully();
		String strPatinetName = objRegisterPatientFlow.createDynamicPatient(DynamicDataFactory.getFirstName(), DynamicDataFactory.getLastName());
		System.out.println("Patinet Name :: "+strPatinetName);
		System.out.println("================================ Dynamic Data Factory ==============================");
		System.out.println("EMAIL :: "+DynamicDataFactory.getRandomEmailId());
		System.out.println("NAME :: "+DynamicDataFactory.getFirstName());
		System.out.println("FULL NAME :: "+DynamicDataFactory.getRandomFullName());
		System.out.println("Phone Number :: "+DynamicDataFactory.getPhoneNumber());
		System.out.println("EMAIL :: "+DynamicDataFactory.getRandomGender());
		System.out.println("================================ END Dynamic Data Factory ==============================");
		if (assertType) {
			DriverFactory.getObjAssertLogUtils().verifyAllStepsAssertion("VERIFY ALL ASSERTIONS ARE PASSED OR NOT FOR TCID  :: "+method.getName());
		} 
		DriverFactory.getObjAssertLogUtils().logReporter("TEST CASE EXECUTION COMPLETED : " + method.getName() + " " + date,true);
	}

}
