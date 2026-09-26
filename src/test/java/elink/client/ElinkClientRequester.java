package elink.client;

import elink.config.TestConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ElinkClientRequester {
    private final String baseUrl;
    private final String login;
    private final String password;

    public ElinkClientRequester() {
        this.baseUrl = "https://" + TestConfig.get("domain") + "/TCatBox/elink/process.json";
        this.login = TestConfig.get("elinkLogin");
        this.password = TestConfig.get("elinkPassword");
    }

    public Response transactions(String ticket, String ccy, String dateFrom, String dateTill, String language, String trnlID) {
        RequestSpecification request = given()
                .auth().preemptive().basic(login, password)
                .formParam("function", "Transactions")
                .formParam("rid", TestConfig.get("rietumuId"));

        if (ticket != null) request.formParam("ticket", ticket);
        if (ccy != null) request.formParam("ccy", ccy);
        if (dateFrom != null) request.formParam("dateFrom", dateFrom);
        if (dateTill != null) request.formParam("dateTill", dateTill);
        if (language != null) request.formParam("language", language);
        if (trnlID != null) request.formParam("trnID", trnlID);

        return request.post(baseUrl);
    }

    public Response outgoingPaymentDetails(String ticket, String refno, String language) {
        RequestSpecification request = given()
                .auth().preemptive().basic(login, password)
                .formParam("function","OutgoingPaymentDetails")
                .formParam("rid",TestConfig.get("rietumuId"));

        if (ticket != null) request.formParam("ticket", ticket);
        if (refno != null) request.formParam("refno", refno);
        if (language != null) request.formParam("language", language);

        return request.post(baseUrl);
    }
}

