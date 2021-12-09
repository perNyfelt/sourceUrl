package se.alipsa.sourceurl;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SourceUrl {

  private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

  private SourceUrl() {
    // static methods only
  }

  private static TrustManager[] trustAllCerts = new TrustManager[]{
      new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }

        public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }
      }
  };

  /**
   * Download the content of the url as a String
   * @param url the URL to the resource to download
   * @param allowSelfSigned whether to allow self-signed certificates or not
   * @return The body content as a string
   * @throws HttpsException if something goes wrong with a https url
   * @throws IOException if something goes wrong with a http url
   */
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
    return getContent(new URL(url));
  }

  private static String getContent(URL url) throws IOException {
    URLConnection con = url.openConnection();
    String charset = extractCharset(con.getContentType(), StandardCharsets.UTF_8.name());
    return IOUtils.toString(url, charset);
  }

  private static String fetchHttps(String urlString, boolean allowSelfSigned) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

    if (allowSelfSigned) {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HostnameVerifier allHostsValid = (hostname, session) -> true;
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
    URL url = new URL(urlString);
    return getContent(url);
  }

  private static String extractCharset(String contentType, String defaultCharset) {
    if (contentType == null)
      return null;
    Matcher m = charsetPattern.matcher(contentType);
    if (m.find()) {
      return m.group(1).trim().toUpperCase();
    }
    return defaultCharset;
  }


}