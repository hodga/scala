/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003, LAMP/EPFL                  **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
** $Id$
\*                                                                      */

package scala;


/** The <code>Iterator</code> object provides various functions for
 *  creating specialized iterators.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 16/07/2003
 */
object Iterator {

  def empty[a] = new Iterator[a] {
    def hasNext = false;
    def next: a = error("next on empty iterator");
  }

  def fromSeq[a](xs: a*) = xs.elements;

  def fromArray[a](xs: Array[a]) = new Iterator[a] {
    private var i = 0;
    def hasNext: Boolean =
      i < xs.length;
    def next: a =
      if (i < xs.length) { val x = xs(i) ; i = i + 1 ; x }
      else error("next on empty iterator");
  }

  def range(lo: Int, hi: Int) = new Iterator[Int] {
    private var i = 0;
    def hasNext: Boolean =
      i <= hi;
    def next: Int =
      if (i <= hi) { i = i + 1 ; i - 1 }
      else error("next on empty iterator");
  }

  def from(lo: Int) = new Iterator[Int] {
    private var i = 0;
    def hasNext: Boolean =
      true;
    def next: Int =
      { i = i + 1 ; i - 1 }
  }
}

/** Iterators are data structures that allow to iterate over a sequence
 *  of elements. They have a <code>hasNext</code> method for checking
 *  if there is a next element available, and a <code>next</code> method
 *  which returns the next element and discards it from the iterator.
 *
 *  @author  Martin Odersky
 *  @version 1.0, 16/07/2003
 */
trait Iterator[+a] with Iterable[a] {
  def hasNext: Boolean;
  def next: a;

  def foreach(f: a => Unit): Unit =
    while (hasNext) { f(next) }

  def forall(p: a => Boolean): Boolean = {
    var res = true;
    while (res && hasNext) { res = p(next); }
    res;
  }

  def exists(p: a => Boolean): Boolean = {
    var res = false;
    while (!res && hasNext) { res = p(next); }
    res;
  }

  def take(n: Int) = new Iterator[a] {
    var remaining = n;
    def hasNext = remaining > 0 && Iterator.this.hasNext;
    def next: a =
      if (hasNext) { remaining = remaining - 1; Iterator.this.next }
      else error("next on empty iterator");
  }

  def drop(n: Int): Iterator[a] = if (n > 0) { next; drop(n - 1); } else this;

  def map[b](f: a => b): Iterator[b] = new Iterator[b] {
    def hasNext = Iterator.this.hasNext;
    def next = f(Iterator.this.next)
  }

  def flatMap[b](f: a => Iterator[b]): Iterator[b] = new Iterator[b] {
    private var cur: Iterator[b] = Iterator.empty;
    def hasNext: Boolean =
      if (cur.hasNext) true
      else if (Iterator.this.hasNext) { cur = f(Iterator.this.next); hasNext }
      else false;
    def next: b =
      if (cur.hasNext) cur.next
      else if (Iterator.this.hasNext) { cur = f(Iterator.this.next); next }
      else error("next on empty iterator");
  }

  def filter(p: a => Boolean): Iterator[a] = new BufferedIterator[a] {
    private val source =
      Iterator.this.buffered;
    private def skip: Unit =
      while (source.hasNext && !p(source.head)) { source.next; () }
    def hasNext: Boolean =
      { skip; source.hasNext }
    def next: a =
      { skip; source.next }
    def head: a =
      { skip; source.head; }
  }

  def zip[b](that: Iterator[b]) = new Iterator[Pair[a, b]] {
    def hasNext = Iterator.this.hasNext && that.hasNext;
    def next = Pair(Iterator.this.next, that.next);
  }

  def buffered: BufferedIterator[a] = new BufferedIterator[a] {
    private var hd: a = _;
    private var ahead: Boolean = false;
    def head: a = {
      if (!ahead) { hd = Iterator.this.next; ahead = true }
      hd
    }
    def next: a =
      if (ahead) { ahead = false; hd }
      else head;
    def hasNext: Boolean =
      ahead || Iterator.this.hasNext;
    override def buffered: BufferedIterator[a] = this;
  }

  def elements: Iterator[a] = this;
}

