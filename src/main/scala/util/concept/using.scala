package util.concept

import java.io.Closeable

/**
  * @author ynupc
  *         Created on 2017/05/01
  */
object using {
  def apply[A](value: A)(implicit closer: Closer[A]) = new using(value, closer)

  implicit val closeable = new Closer[Closeable] {
    def close(value: Closeable): Unit = {
      value.close()
    }
  }

  type TCloseable = {
    def close(): Unit
  }
  implicit val tCloseable = new Closer[TCloseable] {
    def close(value: TCloseable): Unit = {
      value.close()
    }
  }

  type TDisposable = {
    def dispose(): Unit
  }

  implicit val tDisposable = new Closer[TDisposable] {
    def close(value: TDisposable): Unit = {
      value.dispose()
    }
  }

  type TReleasable = {
    def release(): Unit
  }

  implicit val tReleasable = new Closer[TReleasable] {
    def close(value: TReleasable): Unit = {
      value.release()
    }
  }
}

class using[A] private (value: A, closer: Closer[A]) {
  def foreach[B](f: A => B): B = {
    try {
      f(value)
    } finally {
      closer.close(value)
    }
  }
}

trait Closer[-A] {
  def close(value: A): Unit
}
