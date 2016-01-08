# 1.　Stringの文字コード変換
文字コードで文字化けが起こる場合はIOで設定を直すのが一般的でStringはUTF-16BEとして復号された状態ですので、Stringから文字コードを変換するというのはあまり一般的な話ではないですが説明します。
<h3>Charsetの正式名称（Canonical Name）とエイリアス</h3>
<img src="../image/string_course.002.jpeg" width="500px"><br>
文字コードを扱うCharsetクラスには文字コードを代表する正式名称（Canonical Name）とその異表記であるエイリアスが存在します。例えば、正式名称「windows-31j」に対するエイリアスは「MS932」など、正式名称「Shift_JIS」に対するエイリアスは「shift-jis」や「sjis」などです。  
<img src="../image/string_course.003.jpeg" width="500px"><br>
Charsetクラスでよく使われるメソッドをスライドに一覧にしました。いくつかの文字コード（UTF-8、UTF_16、UTF_16BE、	UTF_16LE、US_ASCII、ISO_8859_1）はStandardCharsetsクラスでpublic static変数として存在します。それら以外はCharset.forNameメソッドから取得できます。
サンプルコードを実行すると自身の環境で定義されている全ての文字コードの正式名称とそのエイリアスが標準出力されます。
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
<img src="/image/string_course.004.jpeg" width="500px"><br>
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
