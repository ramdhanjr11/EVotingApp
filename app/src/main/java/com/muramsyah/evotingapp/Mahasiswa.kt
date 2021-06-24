package com.muramsyah.evotingapp

data class Mahasiswa(
    val id: String,
    val nim: String,
    val name: String,
    val force: String,
    val isVote: Boolean,
    val isLogin: Boolean,
    val voteId: String
) {
    constructor() : this("", "", "", "", false, false, "")
}
