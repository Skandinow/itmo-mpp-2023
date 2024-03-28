class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)
    private var c11 by RegularInt(0)
    private var c22 by RegularInt(0)
    private var c33 by RegularInt(0)
    override fun write(time: Time) {
        c11 = time.d1
        c22 = time.d2
        c33 = time.d3
        c3 = c33
        c2 = c22
        c1 = c11
    }

    override fun read(): Time {
        val r1 = c1
        val r2 = c2
        val r3 = c3
        val R1 = Time(r1,r2,r3)
        val r33 = c33
        val r22 = c22
        val r11 = c11
        val R2 = Time(r11,r22,r33)


        return if (R1 == R2) {
            R1
        } else {
            if (r1 == r11) {
                if (r2 == r22) {
                    Time(r1,r2,r33)
                } else {
                    Time(r1,r22,0)
                }
            } else {
                Time(r11, 0, 0)
            }
        }
    }
}