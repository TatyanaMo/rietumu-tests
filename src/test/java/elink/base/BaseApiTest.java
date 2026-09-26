package elink.base;

import elink.client.ElinkClientRequester;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseApiTest {
    protected static ElinkClientRequester elinkClientRequester;

    @BeforeAll
    static void setUpClients() {
        elinkClientRequester = new ElinkClientRequester();
    }

    protected String jsonCode(Response response) {
        return response.jsonPath().getString("code");
    }
}
