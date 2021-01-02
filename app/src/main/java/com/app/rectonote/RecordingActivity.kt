package com.app.rectonote

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


class RecordingActivity : AppCompatActivity() {
    //constant
    private val REC_SAMPLERATE: Int = 8000
    private val REC_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val REC_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private val LOG_TAG = "AudioRecordTest"
    private val PERMISSION_ALL = 1

    private lateinit var txtStatus: TextView
    private lateinit var txtTimer: Chronometer
    private lateinit var modeSelector: RadioGroup
    private lateinit var recordButton: ImageButton
    private lateinit var txtInstr: TextView

    private lateinit var dialog: AlertDialog
    private lateinit var builder: AlertDialog.Builder

    private var isWorking = false
    private var isRecording = false
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var running = false
    private var countdown = false
    private var centisecs = 0


    private var requiredPermissions: Array<String> = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //check if android have permission
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    /*
    fun checkDeniedPermission(context: Context, permissions: Array<String>): List<String> = permissions.filterNot {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    */
    private var startCountdown = 3
    private var isCountDown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
        txtStatus = findViewById<TextView>(R.id.txtStatus)
        txtTimer = findViewById<Chronometer>(R.id.txtTimer)
        modeSelector = findViewById<RadioGroup>(R.id.convertMode)
        recordButton = findViewById(R.id.recordingControl)
        txtStatus.text = "Mic Ready"
        txtInstr = findViewById(R.id.txtInstr)
        recordButton.setOnClickListener(record)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        //startTimer()
        if (!hasPermissions(this, requiredPermissions)) {
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_ALL)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        builder = AlertDialog.Builder(this)
        txtTimer.typeface = ResourcesCompat.getFont(this, R.font.josefin_sans_bold)
        txtTimer.base = SystemClock.elapsedRealtime()

        txtTimer.setOnChronometerTickListener {
            if (isCountDown) {
                if (startCountdown in 1..3) {
                    txtStatus.text = "Start Recording in $startCountdown"
                    startCountdown--
                } else {

                }
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()


        showDialog()
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
        dialog.dismiss()
    }

    private fun showDialog() {
        builder = AlertDialog.Builder(this)
        builder.setTitle("Tip :")
        builder.setMessage("Record on environment as quiet as possible to perform best result.")
        builder.setPositiveButton("OK") { _, _ ->
            // Do something when user press the positive button
        }
        dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ALL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "Recording Permission Required", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }

        }

    }


    private val record = View.OnClickListener {
        if (!isWorking) {
            txtInstr.text = "Recording environment..."
            recording()
            var c = 3
            txtStatus.text
            object : CountDownTimer(2400, 800) {
                override fun onTick(millisUntilFinished: Long) {
                    txtStatus.text = "Start Recording in ${c--}"
                }

                override fun onFinish() {
                    txtStatus.text = "Recording..."
                    txtTimer.base = SystemClock.elapsedRealtime()
                    recordButton.setImageResource(R.drawable.group_345)

                    txtInstr.text = ""
                    txtTimer.start()
                    isWorking = true
                }
            }.start()


        } else {
            txtTimer.stop()
            recordButton.setImageResource(R.drawable.group_344)
            stopRecording()
            Toast.makeText(this, "Recording Complete", Toast.LENGTH_SHORT).show()
            val projectNameFormProjectDetail = intent.getStringExtra("project")
            val intent = Intent(this, AddTrackToProjectActivity::class.java)
            if (projectNameFormProjectDetail != null) {
                intent.putExtra("projectFromProjectDetail", projectNameFormProjectDetail)
            }
            txtStatus.text = "Record Complete"
            val selectedID = modeSelector.checkedRadioButtonId
            val mode = findViewById<RadioButton>(selectedID)
            intent.putExtra("convert_mode", mode.text)
            isWorking = false
            startActivity(intent)
            finish()

        }
    }

    private var bufferElements2Rec = 1024 // want to play 2048 (2K) since 2 bytes we use only 1024
    private var bytesPerElement = 2


    //this function start a stopwatch
    /*
    private fun startTimer() {
        val handler = Handler()
        println("Start")
        handler.post(object : Runnable {
            override fun run() {
                if (countdown){
                    for (i in 3 downTo 1){
                        txtStatus.text =  "Recording in $i..."
                    }
                    countdown = false
                }else{
                    var millisecs = centisecs % 10
                    var minutes = centisecs / 600
                    var secs = (centisecs / 10) % 600
                    var time = String.format(
                        Locale.getDefault(),
                        "%d:%02d:%d", minutes, secs, millisecs
                    )
                    txtTimer.text = time
                    if (running) centisecs++
                    handler.postDelayed(this, 100)
                }

            }
        })

    }
*/

    private fun recording() {

        var bufferSizeInBytes = AudioRecord.getMinBufferSize(
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
            .setBufferSizeInBytes(bufferElements2Rec * bytesPerElement)
            .build()

        recorder!!.startRecording()
        isRecording = true
        recordingThread = Thread({ writeAudioDataToFile() }, "AudioRecorder Thread")
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
        //val filePath = filesDir + "/voice16bit.pcm"
        val filePath = "${filesDir}/voice16bit.pcm"
        var sData = ShortArray(bufferElements2Rec)
        Log.i("file_dir", filePath)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(filePath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
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
            /*
            val f1 = File("/sdcard/voice16bit.pcm") // The location of your PCM file
            val f2 = File("/sdcard/voice16bit.wav") // The location where you want your WAV file
            try {
                rawToWave(f1, f2)
            } catch (e: IOException) {
                e.printStackTrace()
            }*/

        }
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }


    @Throws(IOException::class)
    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = rawFile.readBytes()
        var output: DataOutputStream? = null
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