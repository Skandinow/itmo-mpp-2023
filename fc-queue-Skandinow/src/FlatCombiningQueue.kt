import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * author : Gelmetdinov George
 */
class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)
    
    private val values = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    override fun enqueue(element: E) {
        val cell = randomCellIndex()
        while (true) {
            if (tasksForCombiner.compareAndSet(cell, null, element)) {
                while (!combinerLock.compareAndSet(false, true)) {
                    if (tasksForCombiner.compareAndSet(cell, Result, null)) {
                        return
                    }
                }

                if (tasksForCombiner.compareAndSet(cell, element, null)) {
                    queue.addLast(element)
                    helperFun()
                    return
                }
                tasksForCombiner.compareAndSet(cell, Result, null)
                helperFun()
                return
            }
        }
    }

    @Suppress( "UNCHECKED_CAST")
    override fun dequeue(): E? {
        val cell = randomCellIndex()
        while (true) {
            if (tasksForCombiner.compareAndSet(cell, null, Dequeue)) {
                while (!combinerLock.compareAndSet(false, true)) {
                    if (tasksForCombiner.get(cell) == Result) {
                        val temp = (values.get(cell) as E)
                        tasksForCombiner.compareAndSet(cell, Result, null)
                        return temp
                    }
                }
                if (tasksForCombiner.compareAndSet(cell, Dequeue, null)) {
                    val elem = queue.removeFirstOrNull()
                    helperFun()
                    return elem
                }
                val value = (values.get(cell) as E)
                tasksForCombiner.compareAndSet(cell, Result, null)
                helperFun()
                return value

            }
        }
    }

    @Suppress( "UNCHECKED_CAST")
    private fun helperFun() {
        for (i in 0 until TASKS_FOR_COMBINER_SIZE) {
            val task = tasksForCombiner.get(i)
            if (task == null || task == Result) {
                continue
            }
            if (task == Dequeue) {
                values.set(i, queue.removeFirstOrNull())
            }
            if (tasksForCombiner.compareAndSet(i, Dequeue, Result)) {
                continue
            }
            if (tasksForCombiner.compareAndSet(i, task, Result)) {
                queue.addLast(task as E)
            }
        }
        combinerLock.set(false)
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

private object Dequeue

private class Result<V>(
    val value: V
)