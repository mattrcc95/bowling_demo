import java.util.*
import kotlin.collections.ArrayList

//GLOBAL VARIABLES
const val message: String = "choose a value in range:"
const val errMessage: String = "unacceptable value!"
const val nTotalFrame: Int = 10
const val bound: Int = 10
val sc = Scanner(System.`in`)

class Frame (var id: Int, var frameShots: ArrayList<Int>, var localScore: Int, var bonusShots: Int, var isExpired: Boolean, var isUpdated: Boolean)
class FramePostgre (var score: Int, var flag: String = "")

fun main() {
    val game = arrayListOf<Frame>()
    var currentShot: Int

    for (i in 0 until nTotalFrame) {
        game.add(Frame(i+1, arrayListOf(), 0, 0, false, false))
        while (!game[i].isExpired) {
            val threshold = getThreshold(game[i])
            println("$message [0 : $threshold]")
            currentShot = sc.nextInt()
            if(shotIsValid(currentShot, threshold)) {
                //PREV. FRAMES OPERATIONS => assign bonus score to previous frames whose bonusShot!=0
                assignBonusScore(game, currentShot)
                //CURR. FRAME OPERATIONS => store currentFrame properties, check if it is expired, assign bonus shots
                game[i].frameShots.add(currentShot)
                game[i].localScore += currentShot
                assessCurrentFrameState(game[i])
                //CURR. FRAME + PREV FRAMES OPERATIONS => accumulate score and display the scoreboard
                updateScore(game).forEach { frame -> println(frameDBToString(frame)) }
            } else
                println(errMessage)
        }
        println("frame ${i+1} ends\n")
    }
}