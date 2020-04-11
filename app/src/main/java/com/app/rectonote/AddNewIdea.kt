package com.app.rectonote

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


private const val LOG_TAG = "AudioRecordTest"
private const val PERMISSION_ALL = 1
class AddNewIdea : AppCompatActivity() {
    //constant
    private val REC_SAMPLERATE:Int = 44100
    private val REC_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val REC_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

    private lateinit var btnRecord:Button
    private lateinit var btnStop:Button
    private lateinit var txtStatus:TextView
    private lateinit var txtTimer:TextView

    private var isRecording = false
    private var recorder:AudioRecord? = null
    private var recordingThread: Thread? = null
    private var running = false
    private var centisecs = 0



    private var requiredPermissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    //check if android have permission
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    /*
    fun checkDeniedPermission(context: Context, permissions: Array<String>): List<String> = permissions.filterNot {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_idea)
        btnRecord = findViewById<Button>(R.id.btnRecord)
        btnStop = findViewById<Button>(R.id.btnStop)
        txtStatus = findViewById<TextView>(R.id.txtStatus)
        txtTimer = findViewById<TextView>(R.id.txtTimer)
        txtStatus.text = "Mic Ready"
        btnRecord.setOnClickListener(pressPlay)
        btnStop.setOnClickListener(pressStop)
        startTimer()
        if (!hasPermissions(this, requiredPermissions)) {
                ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_ALL)
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remember")
        builder.setMessage("1. Try to record on environment as quiet as possible to perform best result.\n\n" +
                "2. Please leave silent at least one seconds to let the app record your environment.")
        builder.setPositiveButton("OK"){ _, _ ->
            // Do something when user press the positive button
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        println(requestCode)
        println(grantResults[1])
       when(requestCode){
          PERMISSION_ALL -> {
               if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   // permission was granted, yay! Do the
                   // contacts-related task you need to do.
               } else {
                   Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
                   finish()
               }
               return
           }

       }

    }

    private var bufferElements2Rec = 1024 // want to play 2048 (2K) since 2 bytes we use only 1024
    private var bytesPerElement = 2

    private val pressPlay = View.OnClickListener {
        btnRecord.isEnabled = false
        btnRecord.visibility = View.INVISIBLE
        btnStop.isEnabled = true
        btnStop.visibility = View.VISIBLE
        centisecs = 0
        running = true
        recording()
    }

    private val pressStop = View.OnClickListener {
        btnRecord.isEnabled = true
        btnRecord.visibility = View.VISIBLE
        btnStop.isEnabled = false
        btnStop.visibility = View.INVISIBLE
        running = false;
        stopRecording()
    }
    //this function start a stopwatch
    private fun startTimer(){
        val handler = Handler()
        println("Start")
        handler.post(object : Runnable{
            override fun run() {
                var millisecs = centisecs % 10
                var minutes = centisecs / 600
                var secs = (centisecs / 10) % 600
                var time = String.format(Locale.getDefault(),
                    "%d:%02d:%d", minutes, secs, millisecs)

                txtTimer.text = time
                if(running) centisecs++

                handler.postDelayed(this, 100)
            }
        })

    }


    private fun recording(){

       var  bufferSizeInBytes = AudioRecord.getMinBufferSize(
            REC_SAMPLERATE,
            REC_CHANNELS,
            REC_AUDIO_ENCODING
        )
        // Initialize Audio Recorder.
        // Initialize Audio Recorder.
//        recorder = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            REC_SAMPLERATE,
//            REC_CHANNELS,
//            REC_AUDIO_ENCODING,
//            bufferSizeInBytes
//        )
        recorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(REC_AUDIO_ENCODING)
                    .setSampleRate(REC_SAMPLERATE)
                    .setChannelMask(REC_CHANNELS)
                    .build()
            )
            .setBufferSizeInBytes(bufferElements2Rec*bytesPerElement)
            .build()
        println(recorder);
        recorder!!.startRecording()
        isRecording = true
        recordingThread = Thread(Runnable { writeAudioDataToFile() }, "AudioRecorder Thread")
        recordingThread!!.start()

    }

    private fun short2byte(sData: ShortArray): ByteArray? {
        val shortArrSize = sData.size
        val bytes = ByteArray(shortArrSize * 2)
        for (i in 0 until shortArrSize) {
            bytes[i * 2] = (sData[i].toInt().and(0x000000FF)).toByte() //Least Sig Bytes
            bytes[i * 2 + 1] = (sData[i].toInt().shr(8)).toByte() //Most Sig Byte
            sData[i] = 0
        }
        return bytes
    }
    private fun writeAudioDataToFile() {
        val filePath = "/sdcard/voice16bit.pcm"
        // val filePath = "${Environment.getDataDirectory()}/voice16bit.pcm
        var sData = ShortArray(bufferElements2Rec)

        var outputStream: FileOutputStream ?= null
        try{
            outputStream = FileOutputStream(filePath)
        }catch (e: FileNotFoundException){
            e.printStackTrace();
        }
        while (isRecording) { // gets the voice output from microphone to byte format
            recorder!!.read(sData, 0, bufferElements2Rec)
            try {
                // writes the data to file from buffer
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
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            recordingThread = null

            val f1 = File("/sdcard/voice16bit.pcm") // The location of your PCM file
            val f2 = File("/sdcard/voice16bit.wav") // The location where you want your WAV file
            try {
                rawToWave(f1, f2)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = rawFile.readBytes();
        var output:DataOutputStream ?= null
        try {
            output = DataOutputStream(waveFile.outputStream())
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + rawData.size) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, 44100) // sample rate
            writeInt(output, REC_SAMPLERATE * 2) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawData.size) // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            val shorts = ShortArray(rawData.size / 2)
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
            val bytes: ByteBuffer = ByteBuffer.allocate(shorts.size * 2)
            for (s in shorts) {
                bytes.putShort(s)
            }
            output.write(rawFile.readBytes())
        } finally {
            output?.close()
        }
    }


    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream?, value: Int) {
        output?.write(value shr 0)
        output?.write(value shr 8)
        output?.write(value shr 16)
        output?.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream?, value: Short) {
        output?.write(value.toInt() shr 0)
        output?.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream?, value: String) {
        for (element in value) {
            output?.write(element.toInt())
        }
    }


}