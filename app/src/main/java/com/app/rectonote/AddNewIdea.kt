package com.app.rectonote

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class AddNewIdea : AppCompatActivity() {
    //constant
    private val REC_SAMPLERATE:Int = 8000
    private val REC_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val REC_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

    //element pointer
    private val btnRecord = findViewById<Button>(R.id.btnRecord)
    private val btnStop = findViewById<Button>(R.id.btnStop)
    private val txtStatus = findViewById<TextView>(R.id.txtStatus)
    private val txtTimer = findViewById<TextView>(R.id.txtTimer)

    //flag
    private var isRecording = false
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var running = false
    private var centisecs = 0
    private var wasRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"
        setContentView(R.layout.activity_add_new_idea)
        txtStatus.text = "Mic Ready"
        btnRecord.setOnClickListener(pressPlay)
        btnStop.setOnClickListener(pressStop)
        startTimer()

        val bufferSize = AudioRecord.getMinBufferSize(
            REC_SAMPLERATE,
            REC_CHANNELS, REC_AUDIO_ENCODING
        )
    }

    //permission
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }
    private var bufferElements2Rec = 1024 // want to play 2048 (2K) since 2 bytes we use only 1024
    private var bytesPerElement = 2

    private val pressPlay = View.OnClickListener {
        fun onClick(view:View){
            btnRecord.isEnabled = false
            btnRecord.visibility = View.INVISIBLE
            btnStop.isEnabled = true
            btnRecord.visibility = View.VISIBLE
            centisecs = 0
            running = true

        }
    }

    private val pressStop = View.OnClickListener {
        fun onClick(view : View){
            btnRecord.isEnabled = true
            btnRecord.visibility = View.VISIBLE
            btnStop.isEnabled = false
            btnStop.visibility = View.INVISIBLE
            running = false;
        }
    }

    private fun startTimer(){
        val handler = Handler()
        handler.postDelayed(Runnable {
                var millisecs = centisecs % 10
                var minutes = centisecs / 6000
                var secs = (centisecs / 100) % 6000
                var time = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", minutes, secs, millisecs)
                txtTimer.text = time
                if(running) centisecs++
        },10)
    }


    private fun startRecording(){
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
            REC_SAMPLERATE,
            REC_CHANNELS,
            REC_AUDIO_ENCODING,
            bufferElements2Rec*bytesPerElement)
        recorder!!.startRecording()
        isRecording = true
        recordingThread = Thread(Runnable { writeAudioDataToFile() }, "AudioRecorder Thread")
        recordingThread!!.start()

    }

    private fun short2byte(sData: ShortArray): ByteArray? {
        val shortArrsize = sData.size
        val bytes = ByteArray(shortArrsize * 2)
        for (i in 0 until shortArrsize) {
            bytes[i * 2] = (sData[i].toInt().and(0x000000FF)) as Byte //Least Sig Bytes
            bytes[i * 2 + 1] = (sData[i].toInt().shr(8)) as Byte //Most Sig Byte
            sData[i] = 0
        }
        return bytes
    }
    private fun writeAudioDataToFile() {
        val filePath = "${filesDir}/voice8K16bitmono.pcm"
        var sData = ShortArray(bufferElements2Rec)

        var outputStream: FileOutputStream ?= null
        try{
            outputStream = FileOutputStream(filePath)
        }catch (e: FileNotFoundException){
            e.printStackTrace();
        }
        while (isRecording) { // gets the voice output from microphone to byte format
            recorder!!.read(sData, 0, bufferElements2Rec)
            try { // // writes the data to file from buffer
                // stores the voice buffer
                val bData = short2byte(sData)
                outputStream!!.write(bData, 0, bufferElements2Rec * bytesPerElement)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            outputStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun stopRecording() { // stops the recording activity
        if (null != recorder) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            recordingThread = null
        }
    }


}



