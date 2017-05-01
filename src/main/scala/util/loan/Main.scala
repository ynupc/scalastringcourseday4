package util.loan

import java.nio.file.Paths

import scala.io.Source

/**
  * @author ynupc
  *         Created on 2017/05/01
  */
object Main extends App {
  Control.using(Source.fromFile(Paths.get("doc", "charset.md").toFile)) {
    _.getLines foreach {
      line: String =>
        println(line)
    }
  }
}