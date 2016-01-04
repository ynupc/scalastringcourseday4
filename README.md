<h1>Scalaの文字列処理 - Day 4 Stringの文字コード変換と数値型との相互変換</h1>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.001.jpeg" width="500px"><br>
今日は、Stringの文字コード変換と数値型との相互変換について紹介したいと思います。ご質問や間違いなどのご指摘は下記のコメント欄にお書きください。
***
<h2>1.　Stringの文字コード変換</h2>
<h3>Charsetの正式名称（Canonical Name）とエイリアス</h3>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.002.jpeg" width="500px"><br>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.003.jpeg" width="500px"><br>
```scala
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
    println("Canonical name:")
    println(ms932.name)
    println("Aliases:")
    while (ms932AliasesIterator.hasNext) {
      println(ms932AliasesIterator.next)
    }
    println()
    val utf8Aliases: util.Set[String] = utf8.aliases
    val utf8AliaseIterator = utf8Aliases.iterator
    println("Canonical name:")
    println(utf8.name)
    println("Aliases:")
    while (utf8AliaseIterator.hasNext) {
      println(utf8AliaseIterator.next)
    }
  }
```
***
<h3>文字コードの変換</h3>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.004.jpeg" width="500px"><br>
```scala
  @Test
  def testEncodingConverter(): Unit = {
    val str = ""
    val eucJpToUtf8 = new String(str.getBytes("EUC-JP"), StandardCharsets.UTF_8)
    val toMs932 = new String(str.getBytes, "MS932")
  }
```
***
<h3>コラム：MalformedInputExceptionとUnmappableCharacterExceptionの回避方法</h3>
```scala
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
```
***
<h2>2.　Stringと数値型の相互変換</h2>
数値型といってもCharは数値型ですがStringとCharの相互変換についてはDay 3で取り上げたのでここでは改めて取り上げません。さらにBooleanは数値型ではないですがここでは取り上げます。従って、Boolean、Byte、Short、Int、Long、Float、DoubleとStringとの相互変換について説明します。
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.005.jpeg" width="500px"><br>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.006.jpeg" width="500px"><br>
```scala
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
```
***
<h3>JavaのBooleanラッパークラスのparseBooleanメソッドによる文字列からのBooleanへの変換</h3>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.007.jpeg" width="500px"><br>
```scala
  @Test
  def testParseBoolean(): Unit = {
    assert(java.lang.Boolean.parseBoolean("true"))
    assert(!java.lang.Boolean.parseBoolean("false"))
    assert(!java.lang.Boolean.parseBoolean("true janakereba nandemo iinkai"))
  }
```
***
<h3>N進数表記</h3>
<h4>特定の進数表記</h4>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.008.jpeg" width="500px"><br>
```scala

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
```
<h4>任意の進数表記</h4>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.009.jpeg" width="500px"><br>
```scala
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
```
<h4>文字とN進数表記での数値の相互変換</h4>
<img src="https://github.com/ynupc/scalastringcourse/blob/master/image/day4/string_course.010.jpeg" width="500px"><br>
```scala
  @Test
  def testRadix2(): Unit = {
    val maxRadix: Int = Character.MAX_RADIX
    val minRadix: Int = Character.MIN_RADIX

    assert(maxRadix == 36)
    assert(minRadix == 2)

    val numeric: Char = 'G'
    //'G'のCode Pointを取得する
    val codePoint: Int = numeric.toString.codePointAt(0)

    //'G'のCode Pointは71
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
```
