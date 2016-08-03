# 1.　Stringの文字コード変換
文字コードで文字化けが起こる場合はIOで設定を直すのが一般的でStringはUTF-16BEとして復号された状態ですので、Stringから文字コードを変換するというのはあまり一般的な話ではないですが説明します。
<h3>1.1　Charsetの正式名称とエイリアス</h3>
<img src="../image/string_course.002.jpeg" width="500px"><br>
文字コードを扱う<a href="https://docs.oracle.com/javase/8/docs/api/java/nio/charset/Charset.html" target="_blank">Charset</a>クラスには文字コードを代表する正式名称（Canonical Name）とその異表記であるエイリアスが存在します。例えば、正式名称「windows-31j」に対するエイリアスは「MS932」など、正式名称「Shift_JIS」に対するエイリアスは「shift-jis」や「sjis」などです。  
<img src="../image/string_course.003.jpeg" width="500px"><br>
Charsetクラスでよく使われるメソッドをスライドに一覧にしました。いくつかの文字コード（UTF-8、UTF_16、UTF_16BE、	UTF_16LE、US_ASCII、ISO_8859_1）は<a href="https://docs.oracle.com/javase/8/docs/api/java/nio/charset/StandardCharsets.html" target="_blank">StandardCharsets</a>クラスでpublic static変数として存在します。それら以外はCharset.forNameメソッドから取得できます。
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

    assert(ms932.name == "windows-31j")

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
<h3>1.2　文字コードの変換</h3>
<img src="/image/string_course.004.jpeg" width="500px"><br>
スライドやサンプルコードは（１）UTF-16BEとしてStringが持っている文字列をEUC-JPのByte配列に変換してそれをUTF-8と解釈してUTF-16BEに変換することで文字化けが直りそうな場合や（２）UTF-16BEとしてStringが持っている文字列をByte配列に変換してそれをwindows-31jと解釈してUTF-16BEに変換することで文字化けが直りそうな場合に使えるかもしれません。特に（２）はWindowsのコマンドプロンプトのデフォルトの文字コードがwindows-31jなのでWindows上でProcessを投げるときの文字化け回避に使えます。
```scala
  @Test
  def testEncodingConverter(): Unit = {
    val str = ""
    val eucJpToUtf8 = new String(str.getBytes("EUC-JP"), StandardCharsets.UTF_8)
    val toMs932 = new String(str.getBytes, "MS932")
  }
```
Windowsのコマンドプロンプトの文字コード変更については<a href="#コラムwindowsのコマンドプロンプトの文字コード変更">コラム：Windowsのコマンドプロンプトの文字コード変更</a>、文字コードwindows-31j（MS932）については<a href="#コラムwindows-31jとは">コラム：windows-31jとは</a>、
MalformedInputExceptionとUnmappableCharacterExceptionの回避方法については<a href="#コラムmalformedinputexceptionとunmappablecharacterexceptionの回避方法">コラム：MalformedInputExceptionとUnmappableCharacterExceptionの回避方法</a>を参照ください。
***
<h3>コラム：Windowsのコマンドプロンプトの文字コード変更</h3>
Windowsのコマンドプロンプトの文字コードをUTF-8にしたいときは```chcp 65001```、デフォルトのwindows-31jに戻したいときは```chcp 932```で変更できます。実は、文字コードMS932の932はこのコマンドで打つ番号が由来で、Shift-JISがCP932でその拡張としてwindows-31jというCP932が生まれ、それをJavaがMS932と命名しました。 ```chcp```は"change code page"の略です。他のコードページの番号が知りたい場合は、<a href="https://msdn.microsoft.com/en-us/library/ee719641.aspx" target="_blank">Supported Codepage in Windows</a>をご参照ください。
***
<h3>コラム：windows-31jとは</h3>
1978年に制定されたJIS C 6226を、1982年にシフトさせたShift-JISが開発され、MicrosoftがMS-DOSの日本語文字コードとして採用し、コードページ932に収めた。MicrosoftはOEM（相手先ブランド製造）をNEC、IBM、富士通などと結び、NECのPC-9800シリーズ、IBMのPS/55 シリーズ、富士通のFMRシリーズなどはMS-DOSを搭載し、それぞれがコードページ932に対して独自のベンダー拡張を行いました。このOEMにより生まれたコードページをOEM拡張コードページと呼びます。1993年にMicrosoftがWindows 3.1の日本語版を発売するために、IBMとNEC２社のOEM拡張コードページの差分を吸収して互換性を維持しつつ統一したCP932を開発し、それにあたってOEMメーカがCP932の仕様を変更できないようにしました。この統一されたコードページ932をIANA（Internet Assigned Numbers Authority）に「Windows 3.1 Japanese」を意味する「Windows-31J」として登録しました（<a href="http://www.iana.org/assignments/charset-reg/windows-31J" target="_blank">IANAのwindows-31jの登録</a>）。具体的には、JIS C 6226から1983年と1990年の２度に渡り改正されたJIS X 0208-1990の8,836文字（＝94区×94点）に、NEC特殊文字83文字、NEC選定IBM拡張文字374文字、IBM拡張文字388文字を追加されたものがwindows-31jです。IBMとNECとの互換性を維持するため文字が重複して登録されてしまいました。JIS X 0208-1990の中の1983年の追加分の中の10字、NEC特殊文字の中の22字、NEC選定IBM拡張文字とIBM拡張文字の全ての文字が重複しています。他の文字コードからwindows-31jに変換する場合の文字の優先順位は、JIS X 0208-1990、NEC特殊文字、IBM拡張文字、NEC選定IBM拡張文字とすることになっており、これに従い変換された場合はNEC選定IBM拡張文字は使用されません。windows-31jとUnicodeの変換表は<a href="http://unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP932.TXT" target="_blank">"cp932 to Unicode table"</a>と<a href="ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WindowsBestFit/readme.txt" target="_blank">WindowsBestFit</a>の<a href="ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WindowsBestFit/bestfit932.txt" target="_blank">bestfit932.txt</a>をご参照ください。<br>
<br>
1978年にJIS C 6226を制定<br>
1982年にJIS C 6226をシフトさせたShift-JISを制定<br>
1983年にJIS C 6226を改正したJIS X 0208-1983を制定<br>
1990年にJIS X 0208-1983を改正したJIS X 0208-1990を制定<br>
1993年にWindows 3.1日本語版発売、windows-31jを制定<br>
1997年にJIS X 0208-1990を改正したJIS X 0208-1997を制定<br>
windows-31jがJIS X 0208-1997に対応<br>
<table>
<thead>
<caption>重複文字が含まれる領域</caption>
</thead>
<tbody>
<tr>
<th>字種</th><th>区番号</th><th>コードポイント</th><th>字数</th>
</tr>
<tr>
<td>JIS X 0208-1990由来の文字</td><td>2</td><td>表「JIS X 0208-1990由来の重複文字の一覧」</td><td>10</td>
</tr>
<tr>
<td>NEC特殊文字</td><td>13</td><td>0x8740 - 0x879C</td><td>22</td>
</tr>
<tr>
<td>IBM拡張文字</td><td>115 - 119</td><td>0xFA40 - 0xFC4B</td><td>388（全て）</td>
</tr>
<tr>
<td>NEC選定IBM拡張文字</td><td>89 - 92</td><td>0xED40 - 0xEEFC</td><td>374（全て）</td>
</tr>
</tbody>
</table>
<table>
<thead>
<caption>JIS X 0208-1990由来の重複文字の一覧</caption>
</thead>
<tbody>
<tr>
<th>コードポイント</th><th>文字</th>
</tr>
<tr>
<td>0x81BE</td><td>∪</td>
</tr>
<tr>
<td>0x81BF</td><td>∩</td>
</tr>
<tr>
<td>0x81CA</td><td>￢</td>
</tr>
<tr>
<td>0x81DA</td><td>∠</td>
</tr>
<tr>
<td>0x81DB</td><td>⊥</td>
</tr>
<tr>
<td>0x81DF</td><td>≡</td>
</tr>
<tr>
<td>0x81E0</td><td>≒</td>
</tr>
<tr>
<td>0x81E3</td><td>√</td>
</tr>
<tr>
<td>0x81E6</td><td>∵</td>
</tr>
<tr>
<td>0x81E7</td><td>∫</td>
</tr>
</tbody>
</table>
以上の経緯により、Shift-JISもIBMやNECなどによるOEM拡張コードページのCP932もwindows-31jも全てMS-DOS上ではCP932ではあるわけですが、Javaでは、Shift-JISはShift-JIS、windows-31jのことをMS932、IBM拡張のCP932をCP932としています。<br>
<br>
IAEAにはWindows-31Jが登録されていますが、Microsoftの標準ウェブブラウザInternet ExploreがWindows-31Jというcharsetを認識できないバグがあったため、Windows-31JであってもHTMLなどのcharsetはShift-JISとする悪慣習が存在します。そのため、HTMLなどのウェブ文書のcharsetがShift-JISと表記されていてもWindows-31Jで読み込む必要が生まれました。
***
<h3>コラム：<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/nio/charset/MalformedInputException.html" target="_blank">MalformedInputException</a>と<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/nio/charset/UnmappableCharacterException.html" target="_blank">UnmappableCharacterException</a>の回避方法</h3>
文字コードをIOで設定してもMalformedInputExceptionやUnmappableCharacterExceptionでファイルが読み取れない場合があります。特にウェブ上のHTMLファイルを読み取るときにしばしばこの問題が発生します。この問題が発生した場合は、<a href="http://www.scala-lang.org/api/current/index.html#scala.io.Codec" target="_blank">Codec</a>クラスや<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/nio/charset/CharsetDecoder.html" target="_blank">CharsetDecorder</a>クラス、<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/nio/charset/CharsetEncoder.html" target="_blank">ChrasetEncoder</a>クラスが持つonMalformedInputのメソッドやonUnmappableCharacterメソッドでExceptionを無視したり、特定のCharに置き換えたることができます。なおHTMLのCharsetがShift_JISとなっていた場合だとShift_JISの上位互換のwindows-31jで読み込むだけで上記のExceptionが回避できる場合もあります。
```scala
  @Test
  def testCodec(): Unit = {
    val source: BufferedSource = getSourcefromURL("http://awabi.2ch.net/test/read.cgi/gogaku/1298542858/", "SJIS")
    //println(source.codec)
    for (line <- source.getLines) {
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
