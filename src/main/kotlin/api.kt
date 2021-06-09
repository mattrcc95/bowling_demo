//logic to display the update scoreboard
fun updateScore(game: ArrayList<Frame>) : ArrayList<FramePostgre> {
    val listPostgre = arrayListOf<FramePostgre>()
    if(game.size > 1){
        for (i in 0 until game.size-1) {
            if (game[i].bonusShots == 0 && !game[i+1].isUpdated) {
                game[i+1].localScore += game[i].localScore
                game[i+1].isUpdated = true
            }
        }
    }
    game.forEach { frame -> listPostgre.add(mapFrameToPostgre(frame)) }
    return listPostgre
}

//logic to map a frame object to a framePostgre object
fun mapFrameToPostgre(frame: Frame) : FramePostgre {
    val framePostgre = FramePostgre(0,"")
    framePostgre.score = frame.localScore
    if(frame.frameShots.size == 1){ //1 shot made
        if(frame.frameShots[0] < 10)
            framePostgre.flag = frame.frameShots[0].toString()
        else
            framePostgre.flag = " X "
    } else if(frame.frameShots.size == 2){ // 2 shots made
        if (frame.frameShots[0] + frame.frameShots[1] < 10) //both shots < 10
            framePostgre.flag = frame.frameShots[0].toString() + " - " + frame.frameShots[1].toString()
        if (frame.frameShots[0] + frame.frameShots[1] == 10 && frame.frameShots[1] != 10) //spare
            framePostgre.flag = frame.frameShots[0].toString() + " - / "
        if(frame.frameShots[0] == 10 && frame.frameShots[1] < 10)
            framePostgre.flag = " X " + " - " + frame.frameShots[1].toString()
        if(frame.frameShots[0] == 10 && frame.frameShots[1] == 10)
            framePostgre.flag = " X - X "
    } else { //3 shot made
        if(frame.frameShots[0] == 10){ //first shot: strike
            if(frame.frameShots[1] < 10 && frame.frameShots[1] + frame.frameShots[2] == 10) // spare after first strike
                framePostgre.flag = " X " + " - " + frame.frameShots[1].toString() + " - / "
            else if(frame.frameShots[1] < 10 && frame.frameShots[1] + frame.frameShots[2] < 10) //regular after first strike
                framePostgre.flag = " X " + " - " + frame.frameShots[1].toString() + " - " + frame.frameShots[2].toString()
            else if(frame.frameShots[1] == 10 && frame.frameShots[2] == 10) // double strike after first strike
                framePostgre.flag = " X - X - X "
            else{ //strike after first strike, then regular
                framePostgre.flag = " X - X - " + frame.frameShots[2].toString()
            }
        } else{ //first shot: spare
            if(frame.frameShots[2] == 10) //strike after first spare
                framePostgre.flag = frame.frameShots[0].toString() + " - / " + "-  X "
            else //regular after first spare
                framePostgre.flag = frame.frameShots[0].toString() + " - / - " + frame.frameShots[2].toString()
        }
    }
    return framePostgre
}

//logic to hit the correct number of pins in a frame
fun shotIsValid(shot: Int, maxAchievable: Int) : Boolean {
    if(shot in 0..maxAchievable)
        return true
    return false
}

//logic to get the correct threshold for each valid shot
fun getThreshold(currentFrame: Frame) : Int {
    val localScore = currentFrame.frameShots.fold(0) {sum, shot -> sum + shot}
    return if(currentFrame.id < 10 || (currentFrame.id == 10 && currentFrame.frameShots.size < 2 && localScore < 10)) {
        bound - localScore
    } else if( (currentFrame.id == 10 && currentFrame.frameShots.size == 2 && currentFrame.frameShots[1] < 10))
        2*bound - localScore
    else{
        bound
    }
}

//logic to check if a given frame is expired and, eventually, assigning bonusShot != 0
fun assessCurrentFrameState(currentFrame: Frame) {
    val localScore = currentFrame.frameShots.fold(0) {sum, shot -> sum + shot}
    if(currentFrame.id < 10) {
        if (currentFrame.frameShots.size == 2 || currentFrame.frameShots[0] == 10) {
            currentFrame.bonusShots = getBonus(currentFrame)
            currentFrame.isExpired = true
        }
    } else {
        if(localScore < 10){
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
    val localScore = frame.frameShots.fold(0) {sum, shot -> sum + shot}
    return if(localScore < 10){
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

//function to print the frame which is send to the database
fun frameDBToString(framePostgre: FramePostgre) : String {
    return "flag: ${framePostgre.flag} => score: ${framePostgre.score}"
}