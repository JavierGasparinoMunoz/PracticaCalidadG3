package com.example.metronome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvCurrentBpms: TextView
    private lateinit var addBpmsButton: MaterialButton
    private lateinit var subBpmsButton: MaterialButton
    private lateinit var playBpmsButton: MaterialButton
    private lateinit var currentMeterButton: MaterialButton
    private lateinit var meterScroll: ScrollView
    private lateinit var sheet: BottomSheetDialog
    private lateinit var rhythm: Rhythm
    private lateinit var circleList: ConstraintLayout
    private lateinit var settingsActivity: SettingsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCurrentBpms = findViewById(R.id.currentBpms)
        addBpmsButton = findViewById(R.id.addBpms)
        subBpmsButton = findViewById(R.id.subBpms)
        playBpmsButton = findViewById(R.id.playBpms)
        settingsActivity = SettingsActivity()
        circleList = findViewById(R.id.constraintLayout2)



        val preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE)
        Rhythm.shared = Rhythm(this, preferencias, getAllButtons(circleList))
        rhythm = Rhythm.shared
        ThemeManager.shared = ThemeManager(this)
        tvCurrentBpms.text = rhythm.getBpm().toString()


        val circle = mutableListOf<Button>()
        for (i in 0 until circleList.childCount) {
            val view = circleList.getChildAt(i)
            if (view is Button) {
                circle.add(view)
                for ((index, button) in circle.withIndex()) {
                    button.setOnClickListener {
                        rhythm.skipBeat(index)
                    }
                }
            }
        }

        addBpmsButton.setOnClickListener {
            rhythm.increaseBpm()
            tvCurrentBpms.text = rhythm.getBpm().toString()
        }

        subBpmsButton.setOnClickListener {
            rhythm.decreaseBpm()
            tvCurrentBpms.text = rhythm.getBpm().toString()
        }

        playBpmsButton.setOnClickListener {
            if (rhythm.isPaused()) {
                rhythm.start()
                playBpmsButton.setIconResource(R.drawable.ic_baseline_pause_24)
            } else {
                pauseMetronome()
            }
        }

        currentMeterButton = findViewById(R.id.currentMeter)
        currentMeterButton.text = rhythm.getMeter().toString() + "/4"
        currentMeterButton.setOnClickListener {
            sheet.show()
        }

        val settingsBtn: MaterialButton = findViewById(R.id.settings)
        settingsBtn.setOnClickListener {
            rhythm.stop()
            val i = Intent(this, SettingsActivity::class.java)
            startActivity(i)
        }

        instantiateSheet()
        addTextRhytmChangeListener(tvCurrentBpms as EditText, rhythm)
        ThemeManager.shared.loadSystemTheme()
        updateCompassCircles(circleList, rhythm.getMeter())
    }

    private fun pauseMetronome() {
        rhythm.stop()
        playBpmsButton.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        //rhythm.resetBeatCircles()
    }

    private fun instantiateSheet() {
        sheet = BottomSheetDialog(this)
        sheet.setContentView(R.layout.bottom_signature_drawer)
        sheet.behavior.isDraggable = false;
        meterScroll = sheet.findViewById<ScrollView>(R.id.meterBox) as ScrollView
        addMeterButtonsListeners()
    }

    private fun addMeterButtonsListeners(){
        val meterButtons = getAllButtons(meterScroll)

        for (meterButton in meterButtons) {
            val meter = getMeterFromCompass(meterButton.text)

            meterButton.setOnClickListener {
                pauseMetronome()
                rhythm.setMeter(meter)
                currentMeterButton.text = meterButton.text
                updateCompassCircles(circleList, meter)
                sheet.dismiss()
            }
        }
    }

    fun getAllButtons(layout: ViewGroup): List<MaterialButton> {
        val buttons: MutableList<MaterialButton> = ArrayList()
        for (i in 0 until layout.childCount) {
            val v = layout.getChildAt(i)
            if (v is MaterialButton) {
                buttons.add(v)
            }
            else if (v is ViewGroup){
                buttons.addAll(getAllButtons(v))
            }
        }
        return buttons
    }

    fun getMeterFromCompass(compass:CharSequence):Int{
        val pos = compass.indexOf('/')
        return compass.substring(0,pos).toInt()
    }

    private fun updateCompassCircles(circleList: ConstraintLayout, meter: Int) {
        val parentWidth = circleList.width
        for (i in 0 until circleList.childCount) {
            val v = circleList.getChildAt(i)
            if (v is MaterialButton) {
                if (i < meter) {
                    v.visibility = MaterialButton.VISIBLE
                    v.layoutParams.width = parentWidth / meter
                }
                else {
                    v.visibility = MaterialButton.GONE
                }
            }
        }
    }

    private fun addTextRhytmChangeListener(
        bpmsText: EditText, rhythmHandler: Rhythm
    ) {
        bpmsText.setOnEditorActionListener { textView, actionId, keyEvent ->
            var bpmsStr = bpmsText.text.toString()
            if (actionId == EditorInfo.IME_ACTION_DONE && bpmsStr != "") {
                closeKeyboard()
                rhythmHandler.setBpm(bpmsStr.toInt())
                bpmsText.clearFocus()
                true
            } else {
                bpmsText.setText(rhythm.getBpm().toString())
                rhythmHandler.setBpm(rhythm.getBpm())
                false
            }
        }
    }

    private fun closeKeyboard() {
        var inManager = this.getSystemService(Context.INPUT_METHOD_SERVICE)
        inManager = inManager as InputMethodManager
        inManager.hideSoftInputFromWindow(
            this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}