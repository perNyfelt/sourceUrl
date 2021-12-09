# remember to add export(function name) to NAMESPACE to make them available

sourceUrl <- function(url, allowSelfSigned=FALSE, envir=.GlobalEnv) {
  content <- SourceUrl$fetchContent(url, allowSelfSigned)  
  eval(parse(text=content), envir=envir)
}
