package com.muramsyah.evotingapp.model

data class CalonKahim(
    var name: String,
    var voteCount: Long
) {
    constructor() : this("", 0)
}
