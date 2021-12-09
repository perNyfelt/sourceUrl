package se.alipsa.sourceurl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SourceUrlTest {

  @Test
  public void testHttps() throws HttpsException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, URISyntaxException, KeyManagementException {
    int port = TlsServer.start();
    String body = SourceUrl.fetchContent("https://localhost:" + port + "/test", true);
    assertEquals("print('This is the response'); aVar <- 42", body);
    TlsServer.stop();
  }
}
