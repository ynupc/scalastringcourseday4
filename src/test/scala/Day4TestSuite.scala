import java.io.ByteArrayInputStream
import java.nio.charset.{Charset, CharsetDecoder, CodingErrorAction, StandardCharsets}
import java.util

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

import scala.io.{BufferedSource, Codec, Source}

/**
  * @author ynupc
  *         Created on 2015/12/20
  */
class Day4TestSuite extends AssertionsForJUnit {
  @Test
  def testCharset(): Unit = {
    val defaultCharset: Charset = Charset.defaultCharset
    println(s"Default Charset: ${defaultCharset.name}")
    println()
    println("---")
    val ms932: Charset = Charset.forName("MS932")

    val utf8: Charset = StandardCharsets.UTF_8

    assert(ms932.name() == "windows-31j")

    println("Available Charsets")
    val availableCharsets: util.SortedMap[String, Charset] = Charset.availableCharsets
    for (canonicalName <- availableCharsets.keySet.toArray) {
      println(s"Canonical Name: $canonicalName")
      println("Aliases:")
      val aliasesIterator = availableCharsets.get(canonicalName).aliases.iterator
      while (aliasesIterator.hasNext) {
        println(aliasesIterator.next)
      }
      println()
    }

    println("---")
    val ms932Aliases: util.Set[String] = ms932.aliases
    val ms932AliasesIterator = ms932Aliases.iterator
    println("Canonical Name:")
    println(ms932.name)
    println("Aliases:")
    while (ms932AliasesIterator.hasNext) {
      println(ms932AliasesIterator.next)
    }
    println()
    val utf8Aliases: util.Set[String] = utf8.aliases
    val utf8AliaseIterator = utf8Aliases.iterator
    println("Canonical Name:")
    println(utf8.name)
    println("Aliases:")
    while (utf8AliaseIterator.hasNext) {
      println(utf8AliaseIterator.next)
    }
  }

  @Test
  def testEncodingConverter(): Unit = {
    val str = ""
    val eucJpToUtf8 = new String(str.getBytes("EUC-JP"), StandardCharsets.UTF_8)
    val toMs932 = new String(str.getBytes, "MS932")
  }

  @Test
  def testCodec(): Unit = {
    val source: BufferedSource = getSourcefromURL("http://awabi.2ch.net/test/read.cgi/gogaku/1298542858/", "SJIS")
    //println(source.codec)
    for (line <- source.getLines()) {
      //println(line)
    }
  }

  /**
    * (1) If the encoding was not supported, this method would return BufferedSource which has only an empty string.
    * (2) If the encoding was Shift JIS, this method would use Windows-31J instead of Shift JIS.
    * (3) Ignore
    * java.nio.charset.MalformedInputException
    * (4) Replace a character
    * to a ghost character "彁" on
    * java.nio.charset.UnmappableCharacterException
    * @param url URL
    * @param encoding Charset
    * @return BufferedSource
    */
  private def getSourcefromURL(url: String, encoding: String): BufferedSource = {
    val sjis: Charset = Charset.forName("Shift_JIS")
    val ms932: Charset = Charset.forName("windows-31j")
    val utf8: Charset = StandardCharsets.UTF_8
    val ghost: String = "彁"

    if (!Charset.isSupported(encoding)) {
      return new BufferedSource(
        new ByteArrayInputStream(
          "".getBytes()))
    }

    Charset.forName(encoding) match {
      case `sjis` =>
        implicit val codec: Codec = Codec(ms932).
          onMalformedInput(CodingErrorAction.IGNORE).
          onUnmappableCharacter(CodingErrorAction.REPLACE).
          decodingReplaceWith(ghost)
        Source.fromURL(url)
      case `utf8` =>
        val decoder: CharsetDecoder = utf8.newDecoder.
          onMalformedInput(CodingErrorAction.IGNORE).
          onUnmappableCharacter(CodingErrorAction.REPLACE).
          replaceWith(ghost)
        implicit val codec: Codec = Codec(decoder)
        Source.fromURL(url)
      case otherwise =>
        implicit val codec: Codec = Codec(otherwise).
          onMalformedInput(CodingErrorAction.IGNORE).
          onUnmappableCharacter(CodingErrorAction.REPLACE).
          decodingReplaceWith(ghost)
        Source.fromURL(url)

    }
  }

  @Test
  def testConversionsBetweenStringAndNum(): Unit = {
    assert(10L.toString == "10")
    assert("10".toLong == 10L)
    //java.lang.NumberFormatException

    assert(10.toString == "10")
    assert("10".toInt == 10)
    //java.lang.NumberFormatException

    assert((10: Short).toString == "10")
    assert("10".toShort == (10: Short))
    //java.lang.NumberFormatException

    assert((10: Byte).toString == "10")
    assert("10".toByte == (10: Byte))
    //java.lang.NumberFormatException

    assert(true.toString == "true")
    assert(false.toString == "false")
    assert("true".toBoolean)
    assert(!"false".toBoolean)
    //java.lang.IllegalArgumentException

    assert(0.0D.toString == "0.0")
    assert("0.0".toDouble == 0D)
    //java.lang.NumberFormatException

    assert(2.718F.toString == "2.718")
    assert("2.718".toFloat == 2.718F)
    //java.lang.NumberFormatException
  }

  @Test
  def testParseBoolean(): Unit = {
    assert(java.lang.Boolean.parseBoolean("true"))
    assert(!java.lang.Boolean.parseBoolean("false"))
    assert(!java.lang.Boolean.parseBoolean("true janakereba nandemo iinkai"))
  }

  @Test
  def testGetNumericValue(): Unit = {
    assert(java.lang.Character.getNumericValue('\u216C') == 50)

    assert(java.lang.Character.getNumericValue('0') == 0)
    assert(java.lang.Character.getNumericValue('1') == 1)
    assert(java.lang.Character.getNumericValue('2') == 2)
    assert(java.lang.Character.getNumericValue('3') == 3)
    assert(java.lang.Character.getNumericValue('4') == 4)
    assert(java.lang.Character.getNumericValue('5') == 5)
    assert(java.lang.Character.getNumericValue('6') == 6)
    assert(java.lang.Character.getNumericValue('7') == 7)
    assert(java.lang.Character.getNumericValue('8') == 8)
    assert(java.lang.Character.getNumericValue('9') == 9)

    assert(java.lang.Character.getNumericValue('０') == 0)
    assert(java.lang.Character.getNumericValue('１') == 1)
    assert(java.lang.Character.getNumericValue('２') == 2)
    assert(java.lang.Character.getNumericValue('３') == 3)
    assert(java.lang.Character.getNumericValue('４') == 4)
    assert(java.lang.Character.getNumericValue('５') == 5)
    assert(java.lang.Character.getNumericValue('６') == 6)
    assert(java.lang.Character.getNumericValue('７') == 7)
    assert(java.lang.Character.getNumericValue('８') == 8)
    assert(java.lang.Character.getNumericValue('９') == 9)

    assert(java.lang.Character.getNumericValue('a') == 10)
    assert(java.lang.Character.getNumericValue('b') == 11)
    assert(java.lang.Character.getNumericValue('c') == 12)
    assert(java.lang.Character.getNumericValue('d') == 13)
    assert(java.lang.Character.getNumericValue('e') == 14)
    assert(java.lang.Character.getNumericValue('f') == 15)
    assert(java.lang.Character.getNumericValue('g') == 16)
    assert(java.lang.Character.getNumericValue('h') == 17)
    assert(java.lang.Character.getNumericValue('i') == 18)
    assert(java.lang.Character.getNumericValue('j') == 19)
    assert(java.lang.Character.getNumericValue('k') == 20)
    assert(java.lang.Character.getNumericValue('l') == 21)
    assert(java.lang.Character.getNumericValue('m') == 22)
    assert(java.lang.Character.getNumericValue('n') == 23)
    assert(java.lang.Character.getNumericValue('o') == 24)
    assert(java.lang.Character.getNumericValue('p') == 25)
    assert(java.lang.Character.getNumericValue('q') == 26)
    assert(java.lang.Character.getNumericValue('r') == 27)
    assert(java.lang.Character.getNumericValue('s') == 28)
    assert(java.lang.Character.getNumericValue('t') == 29)
    assert(java.lang.Character.getNumericValue('u') == 30)
    assert(java.lang.Character.getNumericValue('v') == 31)
    assert(java.lang.Character.getNumericValue('w') == 32)
    assert(java.lang.Character.getNumericValue('x') == 33)
    assert(java.lang.Character.getNumericValue('y') == 34)
    assert(java.lang.Character.getNumericValue('z') == 35)

    assert(java.lang.Character.getNumericValue('A') == 10)
    assert(java.lang.Character.getNumericValue('B') == 11)
    assert(java.lang.Character.getNumericValue('C') == 12)
    assert(java.lang.Character.getNumericValue('D') == 13)
    assert(java.lang.Character.getNumericValue('E') == 14)
    assert(java.lang.Character.getNumericValue('F') == 15)
    assert(java.lang.Character.getNumericValue('G') == 16)
    assert(java.lang.Character.getNumericValue('H') == 17)
    assert(java.lang.Character.getNumericValue('I') == 18)
    assert(java.lang.Character.getNumericValue('J') == 19)
    assert(java.lang.Character.getNumericValue('K') == 20)
    assert(java.lang.Character.getNumericValue('L') == 21)
    assert(java.lang.Character.getNumericValue('M') == 22)
    assert(java.lang.Character.getNumericValue('N') == 23)
    assert(java.lang.Character.getNumericValue('O') == 24)
    assert(java.lang.Character.getNumericValue('P') == 25)
    assert(java.lang.Character.getNumericValue('Q') == 26)
    assert(java.lang.Character.getNumericValue('R') == 27)
    assert(java.lang.Character.getNumericValue('S') == 28)
    assert(java.lang.Character.getNumericValue('T') == 29)
    assert(java.lang.Character.getNumericValue('U') == 30)
    assert(java.lang.Character.getNumericValue('V') == 31)
    assert(java.lang.Character.getNumericValue('W') == 32)
    assert(java.lang.Character.getNumericValue('X') == 33)
    assert(java.lang.Character.getNumericValue('Y') == 34)
    assert(java.lang.Character.getNumericValue('Z') == 35)

    assert(java.lang.Character.getNumericValue('ａ') == 10)
    assert(java.lang.Character.getNumericValue('ｂ') == 11)
    assert(java.lang.Character.getNumericValue('ｃ') == 12)
    assert(java.lang.Character.getNumericValue('ｄ') == 13)
    assert(java.lang.Character.getNumericValue('ｅ') == 14)
    assert(java.lang.Character.getNumericValue('ｆ') == 15)
    assert(java.lang.Character.getNumericValue('ｇ') == 16)
    assert(java.lang.Character.getNumericValue('ｈ') == 17)
    assert(java.lang.Character.getNumericValue('ｉ') == 18)
    assert(java.lang.Character.getNumericValue('ｊ') == 19)
    assert(java.lang.Character.getNumericValue('ｋ') == 20)
    assert(java.lang.Character.getNumericValue('ｌ') == 21)
    assert(java.lang.Character.getNumericValue('ｍ') == 22)
    assert(java.lang.Character.getNumericValue('ｎ') == 23)
    assert(java.lang.Character.getNumericValue('ｏ') == 24)
    assert(java.lang.Character.getNumericValue('ｐ') == 25)
    assert(java.lang.Character.getNumericValue('ｑ') == 26)
    assert(java.lang.Character.getNumericValue('ｒ') == 27)
    assert(java.lang.Character.getNumericValue('ｓ') == 28)
    assert(java.lang.Character.getNumericValue('ｔ') == 29)
    assert(java.lang.Character.getNumericValue('ｕ') == 30)
    assert(java.lang.Character.getNumericValue('ｖ') == 31)
    assert(java.lang.Character.getNumericValue('ｗ') == 32)
    assert(java.lang.Character.getNumericValue('ｘ') == 33)
    assert(java.lang.Character.getNumericValue('ｙ') == 34)
    assert(java.lang.Character.getNumericValue('ｚ') == 35)

    assert(java.lang.Character.getNumericValue('Ａ') == 10)
    assert(java.lang.Character.getNumericValue('Ｂ') == 11)
    assert(java.lang.Character.getNumericValue('Ｃ') == 12)
    assert(java.lang.Character.getNumericValue('Ｄ') == 13)
    assert(java.lang.Character.getNumericValue('Ｅ') == 14)
    assert(java.lang.Character.getNumericValue('Ｆ') == 15)
    assert(java.lang.Character.getNumericValue('Ｇ') == 16)
    assert(java.lang.Character.getNumericValue('Ｈ') == 17)
    assert(java.lang.Character.getNumericValue('Ｉ') == 18)
    assert(java.lang.Character.getNumericValue('Ｊ') == 19)
    assert(java.lang.Character.getNumericValue('Ｋ') == 20)
    assert(java.lang.Character.getNumericValue('Ｌ') == 21)
    assert(java.lang.Character.getNumericValue('Ｍ') == 22)
    assert(java.lang.Character.getNumericValue('Ｎ') == 23)
    assert(java.lang.Character.getNumericValue('Ｏ') == 24)
    assert(java.lang.Character.getNumericValue('Ｐ') == 25)
    assert(java.lang.Character.getNumericValue('Ｑ') == 26)
    assert(java.lang.Character.getNumericValue('Ｒ') == 27)
    assert(java.lang.Character.getNumericValue('Ｓ') == 28)
    assert(java.lang.Character.getNumericValue('Ｔ') == 29)
    assert(java.lang.Character.getNumericValue('Ｕ') == 30)
    assert(java.lang.Character.getNumericValue('Ｖ') == 31)
    assert(java.lang.Character.getNumericValue('Ｗ') == 32)
    assert(java.lang.Character.getNumericValue('Ｘ') == 33)
    assert(java.lang.Character.getNumericValue('Ｙ') == 34)
    assert(java.lang.Character.getNumericValue('Ｚ') == 35)

    assert(java.lang.Character.getNumericValue('〇') == 0)

    assert(java.lang.Character.getNumericValue('零') == -1)//0
    assert(java.lang.Character.getNumericValue('一') == -1)//1
    assert(java.lang.Character.getNumericValue('壱') == -1)//1
    assert(java.lang.Character.getNumericValue('壹') == -1)//1
    assert(java.lang.Character.getNumericValue('弌') == -1)//1
    assert(java.lang.Character.getNumericValue('二') == -1)//2
    assert(java.lang.Character.getNumericValue('弐') == -1)//2
    assert(java.lang.Character.getNumericValue('貮') == -1)//2
    assert(java.lang.Character.getNumericValue('貳') == -1)//2
    assert(java.lang.Character.getNumericValue('弍') == -1)//2
    assert(java.lang.Character.getNumericValue('三') == -1)//3
    assert(java.lang.Character.getNumericValue('参') == -1)//3
    assert(java.lang.Character.getNumericValue('參') == -1)//3
    assert(java.lang.Character.getNumericValue('弎') == -1)//3
    assert(java.lang.Character.getNumericValue('四') == -1)//4
    assert(java.lang.Character.getNumericValue('肆') == -1)//4
    assert(java.lang.Character.getNumericValue('亖') == -1)//4
    assert(java.lang.Character.getNumericValue('五') == -1)//5
    assert(java.lang.Character.getNumericValue('伍') == -1)//5
    assert(java.lang.Character.getNumericValue('六') == -1)//6
    assert(java.lang.Character.getNumericValue('陸') == -1)//6
    assert(java.lang.Character.getNumericValue('七') == -1)//7
    assert(java.lang.Character.getNumericValue('柒') == -1)//7
    assert(java.lang.Character.getNumericValue('漆') == -1)//7
    assert(java.lang.Character.getNumericValue('質') == -1)//7
    assert(java.lang.Character.getNumericValue('八') == -1)//8
    assert(java.lang.Character.getNumericValue('捌') == -1)//8
    assert(java.lang.Character.getNumericValue('九') == -1)//9
    assert(java.lang.Character.getNumericValue('玖') == -1)//9
    assert(java.lang.Character.getNumericValue('十') == -1)//10
    assert(java.lang.Character.getNumericValue('拾') == -1)//10
    assert(java.lang.Character.getNumericValue('卄') == -1)//20
    assert(java.lang.Character.getNumericValue('廿') == -1)//20
    assert(java.lang.Character.getNumericValue('卅') == -1)//30
    assert(java.lang.Character.getNumericValue('丗') == -1)//30
    assert(java.lang.Character.getNumericValue('卌') == -1)//40
    assert(java.lang.Character.getNumericValue('百') == -1)//100
    assert(java.lang.Character.getNumericValue('陌') == -1)//100
    assert(java.lang.Character.getNumericValue('佰') == -1)//100
    assert(java.lang.Character.getNumericValue('千') == -1)//1000
    assert(java.lang.Character.getNumericValue('阡') == -1)//1000
    assert(java.lang.Character.getNumericValue('仟') == -1)//1000
    assert(java.lang.Character.getNumericValue('万') == -1)//10^4
    assert(java.lang.Character.getNumericValue('萬') == -1)//10^4
    assert(java.lang.Character.getNumericValue('億') == -1)//10^8
    assert(java.lang.Character.getNumericValue('兆') == -1)//10^12, Intの最大値2147483647より大きい
    assert(java.lang.Character.getNumericValue('京') == -1)//10^16
    assert(java.lang.Character.getNumericValue('垓') == -1)//10^20, Longの最大値9223372036854775807より大きい
    assert(java.lang.Character.getNumericValue(0x25771) == -1)//U+25771 (𥝱)//10^24
    assert(java.lang.Character.getNumericValue('秭') == -1)//10^24
    assert(java.lang.Character.getNumericValue('穣') == -1)//10^28
    assert(java.lang.Character.getNumericValue('溝') == -1)//10^32
    assert(java.lang.Character.getNumericValue('澗') == -1)//10^36
    assert(java.lang.Character.getNumericValue('正') == -1)//10^40, Floatの最大値3.4028235E38より大きい
    assert(java.lang.Character.getNumericValue('載') == -1)//10^44
    assert(java.lang.Character.getNumericValue('極') == -1)//10^48

    assert(java.lang.Character.getNumericValue('分') == -1)//10^-1
    assert(java.lang.Character.getNumericValue('厘') == -1)//10^-2
    assert(java.lang.Character.getNumericValue('釐') == -1)//10^-2
    assert(java.lang.Character.getNumericValue('毛') == -1)//10^-3
    assert(java.lang.Character.getNumericValue('毫') == -1)//10^-3
    assert(java.lang.Character.getNumericValue('糸') == -1)//10^-4
    assert(java.lang.Character.getNumericValue('絲') == -1)//10^-4
    assert(java.lang.Character.getNumericValue('忽') == -1)//10^-5
    assert(java.lang.Character.getNumericValue('微') == -1)//10^-6
    assert(java.lang.Character.getNumericValue('繊') == -1)//10^-7
    assert(java.lang.Character.getNumericValue('沙') == -1)//10^-8
    assert(java.lang.Character.getNumericValue('塵') == -1)//10^-9
    assert(java.lang.Character.getNumericValue('埃') == -1)//10^-10
    assert(java.lang.Character.getNumericValue('渺') == -1)//10^-11
    assert(java.lang.Character.getNumericValue('漠') == -1)//10^-12


    assert(java.lang.Character.getNumericValue('ⅰ') == 1)
    assert(java.lang.Character.getNumericValue('ⅱ') == 2)
    assert(java.lang.Character.getNumericValue('ⅲ') == 3)
    assert(java.lang.Character.getNumericValue('ⅳ') == 4)
    assert(java.lang.Character.getNumericValue('ⅴ') == 5)
    assert(java.lang.Character.getNumericValue('ⅵ') == 6)
    assert(java.lang.Character.getNumericValue('ⅶ') == 7)
    assert(java.lang.Character.getNumericValue('ⅷ') == 8)
    assert(java.lang.Character.getNumericValue('ⅸ') == 9)
    assert(java.lang.Character.getNumericValue('ⅹ') == 10)

    assert(java.lang.Character.getNumericValue('Ⅰ') == 1)//U+2160
    assert(java.lang.Character.getNumericValue('Ⅱ') == 2)
    assert(java.lang.Character.getNumericValue('Ⅲ') == 3)
    assert(java.lang.Character.getNumericValue('Ⅳ') == 4)
    assert(java.lang.Character.getNumericValue('Ⅴ') == 5)
    assert(java.lang.Character.getNumericValue('Ⅵ') == 6)
    assert(java.lang.Character.getNumericValue('Ⅶ') == 7)
    assert(java.lang.Character.getNumericValue('Ⅷ') == 8)
    assert(java.lang.Character.getNumericValue('Ⅸ') == 9)
    assert(java.lang.Character.getNumericValue('Ⅹ') == 10)

    assert(java.lang.Character.getNumericValue('⓪') == 0)//U+2460
    assert(java.lang.Character.getNumericValue('①') == 1)
    assert(java.lang.Character.getNumericValue('②') == 2)
    assert(java.lang.Character.getNumericValue('③') == 3)
    assert(java.lang.Character.getNumericValue('④') == 4)
    assert(java.lang.Character.getNumericValue('⑤') == 5)
    assert(java.lang.Character.getNumericValue('⑥') == 6)
    assert(java.lang.Character.getNumericValue('⑦') == 7)
    assert(java.lang.Character.getNumericValue('⑧') == 8)
    assert(java.lang.Character.getNumericValue('⑨') == 9)
    assert(java.lang.Character.getNumericValue('⑩') == 10)
    assert(java.lang.Character.getNumericValue('⑪') == 11)
    assert(java.lang.Character.getNumericValue('⑫') == 12)
    assert(java.lang.Character.getNumericValue('⑬') == 13)
    assert(java.lang.Character.getNumericValue('⑭') == 14)
    assert(java.lang.Character.getNumericValue('⑮') == 15)
    assert(java.lang.Character.getNumericValue('⑯') == 16)
    assert(java.lang.Character.getNumericValue('⑰') == 17)
    assert(java.lang.Character.getNumericValue('⑱') == 18)
    assert(java.lang.Character.getNumericValue('⑲') == 19)
    assert(java.lang.Character.getNumericValue('⑳') == 20)
  }

  @Test
  def testBinaryOctalHex(): Unit = {
    assert(java.lang.Integer.toBinaryString(10) == "1010")
    assert(java.lang.Integer.toOctalString(10)  == "12")
    assert(java.lang.Integer.toHexString(10)    == "a")

    assert(java.lang.Long.toBinaryString(10L) == "1010")
    assert(java.lang.Long.toOctalString(10L)  == "12")
    assert(java.lang.Long.toHexString(10L)    == "a")

    assert(java.lang.Float.toHexString(6.67408F)  == "0x1.ab242p2")
    assert(java.lang.Double.toHexString(6.67408D) == "0x1.ab242070b8cfcp2")
  }

  @Test
  def testRadix1(): Unit = {
    //10進数の10の4進数表記は22
    assert(java.lang.Integer.toString(10, 4) == "22")
    assert(java.lang.Long.toString(10L, 4)   == "22")

    //4進数の22は10進数で10
    assert(java.lang.Byte.parseByte("22", 4)   == (10: Byte))
    assert(java.lang.Short.parseShort("22", 4) == (10: Short))
    assert(java.lang.Integer.parseInt("22", 4) == 10)
    assert(java.lang.Long.parseLong("22", 4)   == 10L)
  }

  @Test
  def testRadix2(): Unit = {
    val maxRadix: Int = Character.MAX_RADIX
    val minRadix: Int = Character.MIN_RADIX

    assert(maxRadix == 36)
    assert(minRadix == 2)

    val numeric: Char = 'G'
    //'G'のコードポイントを取得する
    val codePoint: Int = numeric.toString.codePointAt(0)

    //'G'のコードポイントは71
    assert(codePoint == 71)

    //Character.digit
    //16進数において'G'は数字ではないため、-1を返す
    assert(Character.digit(numeric,   16) == -1)
    assert(Character.digit(codePoint, 16) == -1)

    //17進数において'G'は16を意味するため、16を返す
    assert(Character.digit(numeric,   17) == 16)
    assert(Character.digit(codePoint, 17) == 16)

    //Character.forDigit
    //10は16進数で'a'として表されることを確認する
    assert(Character.forDigit(10, 16) == 'a')

    //getNumericValue
    //N進数において'G'は16を意味する
    assert(Character.getNumericValue(numeric)   == 16)
    assert(Character.getNumericValue(codePoint) == 16)
  }
}
