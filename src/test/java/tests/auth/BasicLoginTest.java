package tests.auth;

import body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;     // class custom untuk baca property file (misalnya config.properties)

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class BasicLoginTest {

    @Test
    public void login() throws IOException {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        String email = ConfigReader.getProperty("email");
        String password = ConfigReader.getProperty("password");
        String apiKey = ConfigReader.getProperty("xApiKey");

        LoginBody loginBody = new LoginBody(email, password);

        Response response = given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .body(loginBody.getJsonString())
                .when()
                .post("/api/login")
                .then()
                .extract().response();

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());

        int expectedStatus = 201;
        Assert.assertEquals(response.getStatusCode(), expectedStatus);
    }

    @Test
    public void loginFailed() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        String email = ConfigReader.getProperty("email");
        String apiKey = ConfigReader.getProperty("xApiKey");
        String wrongPassword = "";

        LoginBody loginBody = new LoginBody(email, wrongPassword);

        Response response = given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .body(loginBody.getJsonString())
                .when()
                .post("/api/login")
                .then()
                .extract().response();

        System.out.println("Negative Case Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 400);

        String actualMessage = response.jsonPath().getString("error");
        Assert.assertEquals(actualMessage, "Missing password");
    }
}
