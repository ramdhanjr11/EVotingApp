package com.muramsyah.evotingapp.model

data class Mahasiswa(
    val id: String,
    val nim: String,
    val email: String,
    val name: String,
    val force: String,
    val isVote: Boolean,
    val voteId: String
) {
    constructor() : this("", " ", "", "", "", false, "")
}
