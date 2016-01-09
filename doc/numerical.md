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
<h3>JavaのBooleanラッパークラスのparseBooleanメソッドによる文字列からのBooleanへの変換</h3>
<img src="../image/string_course.007.jpeg" width="500px"><br>
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
<img src="../image/string_course.008.jpeg" width="500px"><br>
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
<img src="../image/string_course.009.jpeg" width="500px"><br>
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
<img src="../image/string_course.010.jpeg" width="500px"><br>
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
