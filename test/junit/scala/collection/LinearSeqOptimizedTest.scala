package scala.collection

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert._
import org.junit.Test

@RunWith(classOf[JUnit4])
class LinearSeqOptimizedTest {

  @Test def `t9936 indexWhere`(): Unit = {
    assertEquals(2, "abcde".indexOf('c', -1))
    assertEquals(2, "abcde".indexOf('c', -2))
    assertEquals(2, "abcde".toList.indexOf('c', -1))
    assertEquals(2, "abcde".toList.indexOf('c', -2))
    assertEquals(2, "abcde".toList.indexWhere(_ == 'c', -1))
    assertEquals(2, "abcde".toList.indexWhere(_ == 'c', -2))
  }

  @Test def test_efficientTails_list_SI9892: Unit = {
    val tails = List(1,2,3,4).tails.toList

    assertEquals(tails(0).tail, tails(1))
    assertEquals(tails(0).tail.tail, tails(2))
    assertEquals(tails(1).tail, tails(2))
    assertEquals(tails(3).tail, tails(4))
    assertEquals(tails(4), List())
  }

  @Test def test_efficientTails_stream_SI9892: Unit = {
    val stream = Stream.from(1)
    val tails = stream.tails.toStream
    assertTrue(tails.head eq stream)
  }
}
