import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ApiKtTest {

    @Test //JUnit text example
    fun shotIsValid() {
        val expected = arrayOf(true, true, false, false, true, false, false, true, false, false, false, true)
        val shot = arrayOf(2, 3, 9, 7, 2, 10, 10, 9, 10, 5, 5 ,5)
        val achv = arrayOf(8, 8, 3, 2, 8, 4,  6, 10, 3, 2, 1, 6)

        for(i in shot.indices){
            if(shot[i] in 0..achv[i])
                assertEquals(expected[i], true, "at $i")
            else
                assertEquals(expected[i], false,"at $i")
        }
    }
}