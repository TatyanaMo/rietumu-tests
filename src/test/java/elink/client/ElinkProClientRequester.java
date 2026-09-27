package elink.client;

import elink.config.TestConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static io.restassured.RestAssured.given;

public class ElinkProClientRequester {
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private final String baseUrl;
    private final RestAssuredConfig sslConfig;

    public ElinkProClientRequester() {
        this.baseUrl = "https://" + TestConfig.get("domainPro") + "/TCatBox/elinkpro/Process";
        this.sslConfig = RestAssuredConfig.config().sslConfig(buildForcedCertSslConfig());
    }

    public Response postDocument(String ticket, String language, String doc) {
        RequestSpecification req = given().config(sslConfig)
                .formParam("function", "PostDocument");
        if (ticket != null) req.formParam("ticket", ticket);
        if (language != null) req.formParam("language", language);
        if (doc != null) req.formParam("doc", doc);
        return req.post(baseUrl);
    }

    public Response getDocumentForSign(String ticket, String language, String refNo) {
        RequestSpecification request = given().config(sslConfig)
                .formParam("function", "GetDocumentForSign");
        if (ticket != null) request.formParam("ticket", ticket);
        if (language != null) request.formParam("language", language);
        if (refNo != null) request.formParam("refNo", refNo);
        return request.post(baseUrl);
    }

    public Response postSignedDocument(String ticket, String language, String refNo, String doc) {
        RequestSpecification request = given().config(sslConfig)
                .formParam("function","PostSignedDocument");
        if (ticket != null) request.formParam("ticket", ticket);
        if (language != null) request.formParam("language", language);
        if (refNo != null) request.formParam("refNo", refNo);
        if (doc != null) request.formParam("doc", doc);
        return request.post(baseUrl);
    }

    private SSLConfig buildForcedCertSslConfig() {
        LOGGER.info("Adding certificate for authorization (elink pro account)");
        try {
            String certPath = TestConfig.get("certPath");
            String certPassword = TestConfig.get("certPassword");

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(certPath)) {
                ks.load(is, certPassword.toCharArray());
            }
            String alias = ks.aliases().nextElement();

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, certPassword.toCharArray());
            X509ExtendedKeyManager original = (X509ExtendedKeyManager) kmf.getKeyManagers()[0];

            X509ExtendedKeyManager forced = new X509ExtendedKeyManager() {
                public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
                    return alias;
                }
                public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, javax.net.ssl.SSLEngine engine) {
                    return alias;
                }
                public String[] getClientAliases(String keyType, Principal[] issuers) {
                    return new String[]{alias};
                }
                public X509Certificate[] getCertificateChain(String alias) {
                    return original.getCertificateChain(alias);
                }
                public String[] getServerAliases(String keyType, Principal[] issuers) {
                    return original.getServerAliases(keyType, issuers);
                }
                public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
                    return original.chooseServerAlias(keyType, issuers, socket);
                }
                public PrivateKey getPrivateKey(String alias) {
                    return original.getPrivateKey(alias);
                }
            };

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[]{forced}, tmf.getTrustManagers(), null);

            return SSLConfig.sslConfig().with().sslSocketFactory(
                    new org.apache.http.conn.ssl.SSLSocketFactory(
                            sslContext,
                            org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));

        } catch (Exception e) {
            throw new RuntimeException("Failed to build client-certificate SSL config", e);
        }
    }
}

