package com.app.rectonote.midiplayback

class MIDIEvent(
    noteOn: Boolean,
    channel: Byte,
    noteNumber: Byte,
    velocity: Byte,
    val timeStamp: Long
) {
    val message: ByteArray = byteArrayOf(
        (if (noteOn)
            0x90 + channel
        else
            0x80 + channel
                ).toByte(),
        noteNumber,
        velocity,
    )
}