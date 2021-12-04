package se.alipsa.sourceurl;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SourceUrlTest {

  @Test
  public void testHttps() throws HttpsException, IOException {
    String script = SourceUrl.fetchContent("https://localhost:8443/common/resources/connect.R", true);
    System.out.println(script);
  }
}
