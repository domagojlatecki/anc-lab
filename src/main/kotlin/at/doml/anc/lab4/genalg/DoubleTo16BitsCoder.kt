package at.doml.anc.lab4.genalg

class DoubleTo16BitsCoder(private val lowerBound: Double, private val upperBound: Double) {

    private val step = (this.upperBound - this.lowerBound) / 65536.0

    fun encode(value: Double): Short {
        if (value >= this.upperBound) {
            return -1
        }

        if (value <= this.lowerBound) {
            return 0
        }

        return (((value - this.lowerBound) / this.step) * 65535.0).toShort()
    }

    fun decode(value: Short): Double = this.lowerBound + this.step * (value.toInt() and 0x0000FFFF)
}
