package com.muramsyah.evotingapp.model

data class SetupSystem(
    val dateVote: String,
    val isVote: Boolean
) {
    constructor() : this("", false)
}
