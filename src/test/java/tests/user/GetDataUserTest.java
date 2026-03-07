package tests.user;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetDataUserTest {

    public String token;

    @BeforeClass
    public void setup() throws Exception {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();
    }

    @Test
    public void getDataUser() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        String userId = ConfigReader.getProperty("targetUserId");
        String apiKey = ConfigReader.getProperty("xApiKey");

        Response response = given()
                .baseUri(baseUrl)
                .header("x-api-key", apiKey)
                .pathParam("id", userId)
                .when()
                .get("/users/{id}")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code harusnya 200");

        String actualEmail = response.jsonPath().getString("data.email");
        int actualId = response.jsonPath().getInt("data.id");
        Assert.assertEquals(actualId, Integer.parseInt(userId), "ID User tidak sesuai!");
        Assert.assertTrue(actualEmail.contains("reqres.in"),
                "Email '" + actualEmail + "' tidak mengandung domain '" + "reqres.in" + "'");
    }

    @Test
    public void GetUserNotFound() throws IOException {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        String userId = ConfigReader.getProperty("targetUserIdNotFound");
        String apiKey = ConfigReader.getProperty("xApiKey");

        Response response = given()
                .baseUri(baseUrl)
                .header("x-api-key", apiKey)
                .pathParam("id", userId)
                .when()
                .get("/users/{id}")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 404, "Status code harusnya 404");
        Assert.assertEquals(response.asString(), "{}", "Body respons harusnya JSON kosong {}");
    }

    @Test
    public void GetUserWithParam() throws IOException {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        String apiKey = ConfigReader.getProperty("xApiKey");

        String pageNumber = "2";
        Response response = given()
                .baseUri(baseUrl)
                .header("x-api-key", apiKey)
                .queryParam("page", pageNumber)
                .when()
                .get("/users")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code harusnya 200");

        int actualPage = response.jsonPath().getInt("page");
        int actualPerPage = response.jsonPath().getInt("per_page");

        Assert.assertEquals(actualPage, 2, "Halaman tidak sesuai!");
        Assert.assertEquals(actualPerPage, 6, "Jumlah per_page tidak sesuai!");

        int dataSize = response.jsonPath().getList("data").size();
        Assert.assertEquals(dataSize, 6, "Jumlah data dalam list tidak sesuai!");

        java.util.List<String> lastNames = response.jsonPath().getList("data.last_name");
        java.util.List<String> avatars = response.jsonPath().getList("data.avatar");

        Assert.assertNotNull(lastNames.get(0), "last_name tidak boleh null");
        Assert.assertFalse(avatars.get(0).isEmpty(), "avatar tidak boleh kosong");

        System.out.println("Assertion Berhasil: \n" +
                "- page = 2\n" +
                "- per_page = 6\n" +
                "- data.length = 6\n" +
                "- Setidaknya ada satu user dengan last_name dan avatar.\n" +
                "Tervalidasi Semua ✅");
    }
}
