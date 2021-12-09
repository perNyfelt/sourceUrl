package se.alipsa.sourceurl;

import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class TlsServer {

  static HttpsServer webServer;

  public static int start() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException, URISyntaxException {

    URL url = TlsServer.class.getClassLoader().getResource("munin.p12");
    if (url == null) {
      System.err.println("Failed to find self signed cert");
      throw new FileNotFoundException("Failed to find self signed cert");
    }
    File keystoreFile = new File(url.toURI());
    if (!keystoreFile.exists()) {
      System.err.println("keystoreFile " + keystoreFile.getAbsolutePath() + " does not exist!");
      throw new FileNotFoundException("keystoreFile " + keystoreFile.getAbsolutePath() + " does not exist!");
    }

    char[] storepass = "changeit".toCharArray();
    char[] keypass = "changeit".toCharArray();
    FileInputStream fIn = new FileInputStream(keystoreFile);
    KeyStore keystore = KeyStore.getInstance("PKCS12");
    keystore.load(fIn, storepass);

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(keystore, keypass);

    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(keystore);

    webServer = HttpsServer.create(new InetSocketAddress(0), 0);
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    webServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
      public void configure(HttpsParameters params) {
        SSLContext c = getSSLContext();
        SSLEngine engine = c.createSSLEngine();
        params.setNeedClientAuth(false);
        params.setCipherSuites(engine.getEnabledCipherSuites());
        params.setProtocols(engine.getEnabledProtocols());
        // get the default parameters
        SSLParameters sslParameters = c.getDefaultSSLParameters();
        params.setSSLParameters(sslParameters);
      }
    });
    webServer.createContext("/test", new RequestHandler());
    webServer.setExecutor(null); // creates a default executor
    webServer.start();
    return webServer.getAddress().getPort();
  }

  public static class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
      String response = "print('This is the response'); aVar <- 42";
      HttpsExchange httpsExchange = (HttpsExchange) t;
      t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      t.sendResponseHeaders(200, response.getBytes().length);
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }


  public static void stop() {
    webServer.stop(1);
  }


}
