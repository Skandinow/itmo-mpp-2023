import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.paramgen.LongGen
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.junit.Test
import java.io.Serializable

/**
 * This test checks bank implementation for linearizability.
 */
@Param.Params(
    Param(name = "id", gen = IntGen::class, conf = "0:4"),
    Param(name = "amount", gen = LongGen::class, conf = "1:100")
)
@StressCTest
class LinearizabilityTest {
    private val bank: Bank = BankImpl(5)

    @Operation(params = ["id"])
    fun getAmount(id: Int): Long =
        bank.getAmount(id)

    @get:Operation
    val totalAmount: Long
        get() = bank.totalAmount

    @Operation(params = ["id", "amount"])
    fun deposit(id: Int, amount: Long) =
        wrapIllegalState { bank.deposit(id, amount) }

    @Operation(params = ["id", "amount"])
    fun withdraw(id: Int, amount: Long) =
        wrapIllegalState { bank.withdraw(id, amount) }

    @Operation(params = ["id", "id", "amount"])
    fun transfer(idFrom: Int, idTo: Int, amount: Long) =
        wrapIllegalState { if (idFrom != idTo) bank.transfer(idFrom, idTo, amount); "OK" }

    @Test
    fun test() {
        LinChecker.check(LinearizabilityTest::class.java)
    }
}
data object IllegalState : Serializable

private fun <R : Any> wrapIllegalState(block: () -> R): Any = try {
    block()
} catch (e: IllegalStateException) {
    IllegalState
}