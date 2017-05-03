package util.loan

import java.nio.file.Paths

import scala.io.Source

/**
  * @author ynupc
  *         Created on 2017/05/01
  */
object Main extends App {
  import Control.using

  using(Source.fromFile(Paths.get("doc", "charset.md").toFile)) {
    _.getLines foreach println
  }


  using(Source.fromFile(Paths.get("doc", "charset.md").toFile)) {
    in1 =>
      using(Source.fromFile(Paths.get("doc", "numerical.md").toFile)) {
        in2 =>
          in1.getLines foreach println
          in2.getLines foreach println
      }
  }
}