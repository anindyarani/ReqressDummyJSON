package tests.auth;

import body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class LoginTest {

    @Test
    public void Login() throws IOException {

        String baseUrl = ConfigReader.getProperty("baseUrl");
        String apiKey = ConfigReader.getProperty("xApiKey");
        String email = ConfigReader.getProperty("email");
        String password = ConfigReader.getProperty("password");

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

        Assert.assertEquals(response.getStatusCode(), 201);
    }
}
