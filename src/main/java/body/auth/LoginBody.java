package body.auth;

import org.json.JSONObject;
import utils.ConfigReader;

public class LoginBody {
    private String email;
    private String password;

    // Constructor untuk inisialisasi data
    public LoginBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getJsonString() {
        JSONObject body = new JSONObject();
        body.put("email", this.email);
        body.put("password", this.password);
        return body.toString();
    }
}
