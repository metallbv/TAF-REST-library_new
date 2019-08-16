package ua.com.epam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import ua.com.epam.core.mysql.MySQLClient;
import ua.com.epam.core.rest.RestClient;
import ua.com.epam.service.CleanUpService;
import ua.com.epam.utils.DataFactory;
import ua.com.epam.utils.helpers.LocalDateAdapter;

import java.time.LocalDate;

public class BaseTest {
    //to parse JSON String to needed model (with correct date parsing possibility)
    protected Gson g = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

    protected RestClient client = new RestClient();
    protected DataFactory testData = new DataFactory();
    protected CleanUpService clean = new CleanUpService(client);
    protected int statusCode;

    @BeforeMethod
    public void initialize() {
        client = new RestClient();
        testData = new DataFactory();
        clean = new CleanUpService(client);
    }

    @AfterMethod(alwaysRun = true)
    public void verifyStatus(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        switch (methodName) {
            case "postAuthor":
                Assert.assertEquals(statusCode, 201);
                break;
            case "testDeleteAuthor":
                Assert.assertEquals(statusCode, 204);
                break;
            default:
                Assert.assertEquals(statusCode, 200);
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    //close connection to data base
    @AfterSuite(alwaysRun = true)
    public void closeDataBaseConnection() {
        MySQLClient.closeConnection();
    }
}
