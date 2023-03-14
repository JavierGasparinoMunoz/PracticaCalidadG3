package com.example.metronome

import android.media.MediaPlayer

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Rhythm(
    applicationContext: AppCompatActivity,
    preferencias: SharedPreferences,
    beatCircles: List<MaterialButton>
) {

    companion object {
        lateinit var shared: Rhythm
    }

    private var beatSound: MediaPlayer = MediaPlayer.create(
        applicationContext, R.raw.beatclick
    )
    private var specialSound: MediaPlayer = MediaPlayer.create(
        applicationContext, R.raw.accent
    )
    private var muteSound: MediaPlayer = MediaPlayer.create(
        applicationContext, R.raw.mute
    )
    private var beatCircles = beatCircles
    private var inactiveColor = ContextCompat.getColorStateList(applicationContext, R.color.cyan)
    private var activeColor = ContextCompat.getColorStateList(applicationContext, R.color.black)

    private var skippedColor = ContextCompat.getColorStateList(applicationContext, android.R.color.darker_gray)
    private var skippedBeat = BooleanArray(beatCircles.size)


    val editor = preferencias.edit()

    private var bpm: Int = preferencias.getInt("bpm", 100)
    private var meter: Int = preferencias.getInt("meter", 4)

    private var paused = true
    private var skipped = true
    private var threadIsRunning: Boolean = false

    //Inicia el metronomo hasta que este se le indica que pare
    fun start() {
        paused = false
        if (!threadIsRunning)
            GlobalScope.launch {
                playBeatsUntilPaused()
                threadIsRunning = false
            }
    }
    fun stop(){
        paused = true
    }

    //Indicamos que el metronomo no va a parar hasta que se pause
    suspend fun playBeatsUntilPaused() {

        var currentBeat = 0
        while (!isPaused()) {
            threadIsRunning = true
            getNextSound(currentBeat).start()
            updateBeatCircles(currentBeat, meter)
            currentBeat++

            if (currentBeat == meter) currentBeat = 0

            delay(getMsBetweenBeats())
        }
    }

    //actualizamos los BeatCircles
    private fun updateBeatCircles(currentBeat: Int, meter: Int) {
        var prevBeat = if (currentBeat == 0) {
            meter - 1
        } else { currentBeat - 1 }

        if (!skippedBeat[prevBeat]) {
            beatCircles[prevBeat].iconTint = inactiveColor
        }
        if (!skippedBeat[currentBeat]) {
            beatCircles[currentBeat].iconTint = activeColor
        }
    }


    fun resetBeatCircles() {
        for (i in beatCircles.indices) {
            beatCircles[i].iconTint = inactiveColor
            skippedBeat[i]=!isSkipped()
        }
    }


    fun skipBeat(index:Int) {
        if (skippedBeat[index]) {
            skippedBeat[index] = !isSkipped()
            beatCircles[index].iconTint = inactiveColor
        } else {
            skippedBeat[index] = isSkipped()
            beatCircles[index].iconTint = skippedColor
        }
    }

    //Accedemos al siguiente sonido
    private fun getNextSound(currentBeat: Int): MediaPlayer {
        return when {
            skippedBeat[currentBeat] -> muteSound
            currentBeat == 0 -> specialSound
            else -> beatSound
        }
    }

    fun setMeter(meter: Int) {
        this.meter = meter
        editor.putInt("meter", meter)
        editor.commit()
    }

    /**
     * Don't use for creating logical functions outside the Rhythm class, use only for display.
     */
    fun getMsBetweenBeats(): Long = (60000.0 / bpm.toDouble()).toLong()

    fun isPaused(): Boolean {
        return paused
    }

    fun isSkipped():Boolean{
        return skipped
    }

    fun getMeter() : Int{
        return meter
    }

    fun getBpm(): Int {
        return bpm
    }
    fun setBpm(bpm: Int) {
        this.bpm = bpm
        editor.putInt("bpm", this.bpm)
        editor.commit()
    }
    fun increaseBpm() {
        this.bpm ++
        editor.putInt("bpm", this.bpm)
        editor.commit()
    }
    fun decreaseBpm() {
        this.bpm--
        editor.putInt("bpm", this.bpm)
        editor.commit()
    }

}