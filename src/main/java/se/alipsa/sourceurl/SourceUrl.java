package se.alipsa.sourceurl;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class SourceUrl {

  private SourceUrl() {
    // static methods only
  }

  public static String fetchContent(String url, boolean allowSelfSigned) throws HttpsException, IOException {
    if (url == null) {
      throw new IllegalArgumentException("url parameter cannot be null");
    }
    try {
      if (url.startsWith("https")) {
        return fetchHttps(url, allowSelfSigned);
      } else {
        return fetchHttp(url);
      }
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
      throw new HttpsException(e);
    }
  }

  private static String fetchHttp(String url) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    return getContent(url, httpClient);
  }

  private static String getContent(String url, CloseableHttpClient httpClient) throws IOException {
    HttpGet httpGet = new HttpGet(url);
    try (CloseableHttpResponse sslResponse = httpClient.execute(httpGet);
         InputStream is = sslResponse.getEntity().getContent()) {

      Header[] headers = sslResponse.getHeaders("Content-Type");
      Header contentTypeHeader = Arrays.stream(headers).filter(h -> h.getValue().toLowerCase().contains("charset")).findAny().orElse(null);

      String charset = contentTypeHeader == null ? StandardCharsets.UTF_8.name() : extractCharset(contentTypeHeader.getValue());
      return IOUtils.toString(is, charset);
    }
  }

  private static String fetchHttps(String url, boolean allowSelfSigned) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    CloseableHttpClient httpClient;
    if (allowSelfSigned) {
      SSLContext sslContext = SSLContexts.custom()
          .loadTrustMaterial(new TrustSelfSignedStrategy())
          .build();
      SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
      Registry<ConnectionSocketFactory> reg =
          RegistryBuilder.<ConnectionSocketFactory>create()
              .register("https", socketFactory)
              .build();
      HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
      httpClient = HttpClients.custom()
          .setConnectionManager(cm)
          .build();

    } else {
      httpClient = HttpClients.createDefault();
    }
    return getContent(url, httpClient);
  }

  private static String extractCharset(String contentTypeString) {
    try {
      ContentType contentType = ContentType.parse(contentTypeString);
      return contentType.getCharset().name();
    } catch (UnsupportedCharsetException e) {
      return e.getCharsetName(); // extract unsupported charsetName
    }
  }
}