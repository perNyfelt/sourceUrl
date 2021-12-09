library('hamcrest')
library('se.alipsa:sourceUrl')

test.https <- function() {
  import(se.alipsa.sourceurl.TlsServer)
  port <- TlsServer$start()
  sourceUrl(paste0("https://localhost:", port, "/test"), allowSelfSigned = TRUE)
  assertThat(aVar, equalTo(42))
  TlsServer$stop()
}