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
      RequestSpecification req = given()
              .auth().preemptive().basic(login,password)
              .formParam("function", "Transactions")
              .formParam("rid",TestConfig.get("rietumuId"));

      if (ticket != null) req.formParam("ticket",ticket);
      if (ccy != null) req.formParam("ccy",ccy);
      if (dateFrom != null) req.formParam("dateFrom",dateFrom);
      if (dateTill != null) req.formParam("dateTill",dateTill);
      if (language != null) req.formParam("language",language);
      if (trnlID != null) req.formParam("trnID",trnlID);

      return req.post(baseUrl);
  }
}

