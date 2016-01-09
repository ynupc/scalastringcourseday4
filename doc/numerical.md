# 2.　Stringと数値型の相互変換
数値型といってもCharは数値型ですがStringとCharの相互変換についてはDay 3で取り上げたのでここでは改めて取り上げません。さらにBooleanは数値型ではないですがここでは取り上げます。従って、Boolean、Byte、Short、Int、Long、Float、DoubleとStringとの相互変換について説明します。  
<img src="../image/string_course.005.jpeg" width="500px"><br>
JavaでStringと数値型を変換するには、数値型が参照型ではなく特殊なプリミティブ型であるために学ぶべきことが多くあります。  
<img src="../image/string_course.006.jpeg" width="500px"><br>
一方でScalaは数値型も参照型であるために簡単に変換が可能です。ただし、<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/lang/RuntimeException.html" target="_blank">RuntimeException</a>の一種である<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/lang/NumberFormatException.html" target="_blank">NumberFormatException</a>（Booleanの場合のみ数値型ではないため<a href="http://docs.oracle.com/javase/jp/8/docs/api/java/lang/IllegalArgumentException.html" target="_blank">IllegalArgumentException</a>）に注意が必要です。RuntimeExceptionは非検査例外と呼ばれ発生した場合は強制的にシステムが終了する。RuntimeExceptionではないExceptionは検査例外と呼ばれ、例外が発生した場合try-catch文でcatchされ、catch内で例外処理を書くことができる。Scalaで扱う検査例外はJava由来のものでありScala独自の検査例外は存在しません。Javaの検査例外についての問題点は<a href="http://qiita.com/Kokudori/items/0fe9181d8eec8d933c98" target="_blank">検査例外再考</a>をご覧下さい。
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
<h3>2.1　JavaのBooleanラッパークラスのparseBooleanメソッドによる文字列からのBooleanへの変換</h3>
<img src="../image/string_course.007.jpeg" width="500px"><br>
java.lang.BooleanのparseBooleanメソッドは大文字（Upper Case）や小文字（Lower Case）といったケースを無視して"true"の場合は```true```、それ以外は全て```false```を返します。StringクラスのtoBooleanメソッド（厳密にはScalaのStringはStringLikeでimplicit class に暗黙的に拡張されており、StringLike内でtoBooleanメソッドは実装されている）では、ケースを無視して"true"、"false"以外の場合は非検査例外IllegalArgumentExceptionが発生します。
```scala
  @Test
  def testParseBoolean(): Unit = {
    assert(java.lang.Boolean.parseBoolean("true"))
    assert(!java.lang.Boolean.parseBoolean("false"))
    assert(!java.lang.Boolean.parseBoolean("true janakereba nandemo iinkai"))
  }
```
***
<h3>2.2　N進数表記</h3>
数値型をStringに変換するとき、一般的なtoStringメソッドで変換すると１０進数表記になります。そして、Stringから数値型に変換するとき、一般的なtoIntメソッドやtoFloatメソッドなどで変換するとStringを１０進数表記として変換する。数値型から１０進数以外のN進数表記のStringに変換する方法と１０進数以外のN進数表記のStringから数値型に変換する方法について説明します。
<h4>2.2.1　特定の進数表記</h4>
<img src="../image/string_course.008.jpeg" width="500px"><br>
java.lang.Integer、java.lang.Long、java.lang.Float、java.lang.Doubleには特定の進数表記に変換するメソッドが用意されています。
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
<h4>2.2.2　任意の進数表記</h4>
<img src="../image/string_course.009.jpeg" width="500px"><br>
java.lang.Integer、java.lang.Longは任意のN進数表記のStringに変換するtoStringメソッドを持っています。そして、java.lang.Byte、java.lang.Short、java.lang.Integer、java.lang.Longは任意のN進数表記のStringから数値型に変換するメソッドを持っています。
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
<h4>2.2.3　文字とN進数表記での数値の相互変換</h4>
<img src="../image/string_course.010.jpeg" width="500px"><br>
Character.digitメソッドで文字（Charやコードポイント）をN進数表記と解釈して数値型に変換できます。
Character.digitメソッドは第二引数で与えられた基数Nで定義されない文字の変換については-1を返します。
Character.getNumericValueメソッドは数字を数値に変換するメソッドで、N=36の場合のCharacter.digitメソッド似た振舞いをします。なぜ36かというと、0-9の10文字とa-zの26文字を合計して36文字が一般的にN進数表記で使用される文字だからです。定義される基数Nの最大値Character.MAX_RADIXにも36が格納されています。基数36を与えたCharacter.digitメソッドとCharacter.getNumericValueメソッドの違いは、Character.getNumericValueメソッドはN進数と関係ない数字（ローマ数字で50を表す'\u216C'、漢数字で100を表す'百'など）に対しても数値を返す点と文字が数字ではない場合は-1を返しますが文字が数字であっても正の整数を表さない場合は-2を返す点です。
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
