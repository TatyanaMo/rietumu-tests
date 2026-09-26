package elink.base;

import elink.client.ElinkClientRequester;
import elink.client.ElinkProClientRequester;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseApiTest {
    protected static ElinkClientRequester elinkClientRequester;
    protected static ElinkProClientRequester elinkProClientRequester;

    @BeforeAll
    static void setUpClients() {
        elinkClientRequester = new ElinkClientRequester();
        elinkProClientRequester = new ElinkProClientRequester();
    }

    protected String jsonCode(Response response) {
        return response.jsonPath().getString("code");
    }

    protected String loadDocument(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Document not found on classpath: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load document: " + path, e);
        }
    }


}
