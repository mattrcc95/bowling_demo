//logic to display step by step all the frames, accounting for the score accumulation
fun getNewScoreBoard(scoreBoard: ArrayList<Frame>, game: ArrayList<Frame>) : ArrayList<Frame> {
    var sb = scoreBoard //create a copy because kotlin does not allow function parameter modification
    val diffSize: Int
    return if(sb != getCompletedFrames(game)) {
        diffSize = getCompletedFrames(game).size - sb.size
        println("diff size: $diffSize")
        sb = getCompletedFrames(game)
        if(sb.size > 1){
            if(diffSize == 1 || (diffSize == 2 && sb.size == 2)) {
                sb[sb.lastIndex].localScore += sb[sb.lastIndex-1].localScore
            } else{
                sb[sb.lastIndex - 1].localScore += sb[sb.lastIndex - 2].localScore
                sb[sb.lastIndex].localScore += sb[sb.lastIndex - 1].localScore
            }
        }
        sb
    } else
        sb
}

//logic to get the completed frames
private fun getCompletedFrames(game: ArrayList<Frame>) : ArrayList<Frame> {
    val framesToDisplay = game.filter { frame -> (frame.isExpired && frame.bonusShots == 0) }
    return framesToDisplay as ArrayList<Frame>
}

//logic to hit the correct number of pins in a frame
fun shotIsValid(shot: Int, maxAchievable: Int) : Boolean {
    if(shot in 0..maxAchievable)
        return true
    return false
}

//logic to get the correct threshold for each valid shot
fun getThreshold(currentFrame: Frame) : Int {
    return if(currentFrame.id < 10) {
        bound - currentFrame.localScore
    } else{
        return if(currentFrame.frameShots.size == 0 || (currentFrame.frameShots.size == 1 && currentFrame.localScore < 10)){
            bound - currentFrame.localScore
        } else bound
    }
}

//logic to check if a given frame is expired and, eventually, assigning bonusShot != 0
fun assessCurrentFrameState(currentFrame: Frame) {
    if(currentFrame.id < 10) {
        if (currentFrame.frameShots.size == 2 || currentFrame.frameShots[0] == 10) {
            currentFrame.bonusShots = getBonus(currentFrame)
            currentFrame.isExpired = true
        }
    } else {
        if(currentFrame.localScore < 10){
            if(currentFrame.frameShots.size == 2) {
                currentFrame.isExpired = true
            }
        } else{
            if(currentFrame.frameShots.size == 3){
                currentFrame.isExpired = true
            }
        }
    }
}

//logic to assign the correct bonus to each frame completed
private fun getBonus(frame: Frame) : Int {
    return if(frame.localScore < 10){
        0
    } else{
        if(frame.frameShots.size == 1){
            2
        } else{
            1
        }
    }
}

//logic to assign the associated score for each frame whose bonusShot != 0
fun assignBonusScore(game: ArrayList<Frame>, currentShot: Int) {
    for(frame in game) {
        if (frame.bonusShots > 0) {
            frame.localScore += currentShot
            --frame.bonusShots
        }
    }
}

//function to print a single frame
fun frameToString(frame: Frame) : String {
    return "#${frame.id} :: ${frame.frameShots} => ${frame.localScore}, bonusShots: ${frame.bonusShots}"
}