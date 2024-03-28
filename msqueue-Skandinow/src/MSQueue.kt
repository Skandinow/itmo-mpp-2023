import java.util.concurrent.atomic.*

/**
 * @author Gelmetdinov George
 */
class MSQueue<E> : Queue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = AtomicReference(dummy)
        tail = AtomicReference(dummy)
    }

    override fun enqueue(element: E) {

        while (true) {
            val node = Node(element)
            val tail = this.tail.get()
            if (tail.next.compareAndSet(null, node)) {
                this.tail.compareAndSet(tail, node)
                return
            } else {
                this.tail.compareAndSet(tail, tail.next.get())
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val curHead = head.get()
            val curHeadNext = curHead.next.get() ?: return null

            if (head.compareAndSet(curHead, curHeadNext)) {
                val element = curHeadNext.element
                curHeadNext.element = null
                return element
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
