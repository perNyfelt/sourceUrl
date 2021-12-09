# SourceUrl

This is a Renjin package to make it possible to source an url, including https and allowing self-signed certificates.

You add it to you pom file as follows:
```xml
<dependency>
    <groupId>se.alipsa</groupId>
    <artifactId>sourceUrl</artifactId>
    <version>1.0.0</version>
</dependency>
```
The sourceUrl function is defined as follows:
```r
sourceUrl <- function(url, allowSelfSigned=FALSE, envir=.GlobalEnv) {}
```

You can use it as follows:
1. Assuming that the web server has some R code that you want to source at the following location `https://somewhere.com/myscript.R`.
2. The content of myscript.R is as follows:
```r
meaningOfLife <- function() {
  42
}
```
3. You can the source and use it like this:
```r
library("se.alipsa:sourceUrl")
sourceUrl("https://somewhere.com/myscript.R")
print(paste("The answer to the ultimate question is", meaningOfLife()))
```
which will output
```
[1] "The answer to the ultimate question is 42"
```

## Parameters

__url__ (Mandatory) A http or https url (it may or may not work with other types as well) to GET the content from

__allowSelfSigned__ (Optional, default=FALSE) a boolean to allow self-signed certificates to work

__envir__ (Optional, default=.GlobalEnv) The environment you want to evaluate (run) the code in.


# Version history

## 1.0 Initial version, 2021-12-09