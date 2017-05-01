package util.concept

import java.nio.file.Paths

import scala.io.Source

/**
  * @author ynupc
  *         Created on 2017/05/01
  */
object Main extends App {
  import using.closeable
  for {
    in1 <- using(Source.fromFile(Paths.get("doc", "charset.md").toFile))
    in2 <- using(Source.fromFile(Paths.get("doc", "numerical.md").toFile))
  } {
    in1.getLines foreach println
    in2.getLines foreach println
  }
}
