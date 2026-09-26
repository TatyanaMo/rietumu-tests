package elink.signing;

import elink.config.TestConfig;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;

public class XmlDocSigner {
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    public XmlDocSigner() {
        try {
            String certPath = TestConfig.get("certPath");
            String certPassword = TestConfig.get("certPassword");

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(certPath)) {
                if (is == null) {
                    throw new RuntimeException("Certificate not found on classpath: " + certPath);
                }
                ks.load(is, certPassword.toCharArray());
            }

            String alias = ks.aliases().nextElement();
            this.privateKey = (PrivateKey) ks.getKey(alias, certPassword.toCharArray());
            this.certificate = (X509Certificate) ks.getCertificate(alias);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load certificate for signing", e);
        }
    }

    public String sign(String xml) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);
            SignedInfo signedInfo = fac.newSignedInfo(
                    fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments",
                            (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));

            KeyInfoFactory kif = fac.getKeyInfoFactory();
            PublicKey publicKey = certificate.getPublicKey();
            KeyValue keyValue = kif.newKeyValue(publicKey);
            X509Data x509Data = kif.newX509Data(Collections.singletonList(certificate));
            KeyInfo keyInfo = kif.newKeyInfo(Arrays.asList(keyValue, x509Data));

            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            fac.newXMLSignature(signedInfo, keyInfo).sign(dsc);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign XML document", e);
        }
    }
}

