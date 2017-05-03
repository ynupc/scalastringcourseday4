package util.concept

import java.io.Closeable

/**
  * @author ynupc
  *         Created on 2017/05/01
  */
object using {
  def apply[A](resource: A)(implicit closer: Closer[A]) = new using(resource, closer)

  implicit val closeable = new Closer[Closeable] {
    def close(resource: Closeable): Unit = {
      resource.close()
    }
  }

  type TCloseable = {
    def close(): Unit
  }
  implicit val tCloseable = new Closer[TCloseable] {
    def close(resource: TCloseable): Unit = {
      resource.close()
    }
  }

  type TDisposable = {
    def dispose(): Unit
  }

  implicit val tDisposable = new Closer[TDisposable] {
    def close(resource: TDisposable): Unit = {
      resource.dispose()
    }
  }

  type TReleasable = {
    def release(): Unit
  }

  implicit val tReleasable = new Closer[TReleasable] {
    def close(resource: TReleasable): Unit = {
      resource.release()
    }
  }
}

class using[A] private (resource: A, closer: Closer[A]) {
  def foreach[B](f: A => B): B = {
    Option(resource) match {
      case Some(res) =>
        try {
          f(res)
        } finally {
          closer.close(res)
        }
      case None =>
        throw new Exception()
    }
  }
}

trait Closer[-A] {
  def close(resource: A): Unit
}
