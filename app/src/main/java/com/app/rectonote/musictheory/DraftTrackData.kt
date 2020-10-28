package com.app.rectonote.musictheory

data class DraftTrackData(
    val key: Key,
    val tempo: Int,
    val trackType: String,
    val trackSequence: ArrayList<out Note>
) {

    override fun toString(): String {
        var quote = key.reduced
        quote += "\n"
        trackSequence.forEach {
            quote += it.toString() + "\n"
        }
        return quote
    }
}
