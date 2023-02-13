package com.example.metronome


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

typealias PresetsData = Triple<String, Int, Int>

class SettingsActivity : AppCompatActivity()  {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        setNavigationLogic()
        setThemeButtonLogic()
        // FIXME preset manipulation logic should be uncoupled
        setPresetSavingLogic()
        setPresetViewerLogic()
        setAboutAppLogic()
    }

    private fun setNavigationLogic() {
        val settingsNav: MaterialToolbar = findViewById(R.id.settingsToolbar)
        settingsNav.setNavigationOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i, ActivityOptions.makeScaleUpAnimation(
                settingsNav, 0, 0, settingsNav.width, settingsNav.height
            ).toBundle())
        }
    }

    private fun setThemeButtonLogic() {
        val dmButton: Button = findViewById(R.id.dmbtn)

        val checkedItem = intArrayOf(-1)
        dmButton.setOnClickListener {
            val themeBuilder = AlertDialog.Builder(this@SettingsActivity)
            val theme = arrayOf("Light","Dark","System")
            themeBuilder.setTitle("Choose a theme")
            themeBuilder.setSingleChoiceItems(
                theme, checkedItem[0]
            )  { _, which -> ThemeManager.shared.setSystemTheme(theme[which]) }

            themeBuilder.setNegativeButton("Cancel") { dialog, which -> }

            themeBuilder.create().show()
        }
    }

    private fun setPresetSavingLogic() {
        val saveButton: Button = findViewById(R.id.saveCurrentPreset)
        saveButton.setOnClickListener(this::openPresetSavingDialog)
    }

    private fun openPresetSavingDialog(it: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Introduce el nombre")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val presetName = input.text.toString()
            saveOptions(
                presetName,
                Rhythm.shared.getMeter().toString(),
                Rhythm.shared.getBpm().toString()
            )
        }
        builder.setNegativeButton("Cancelar") { _, _ ->
        }

        builder.show()

    }

    // FIXME the rhythm data should be structured in some way, so you dont need
    // to hardcode it
    private fun saveOptions(name: String, buttonText: String, textViewText: String) {
        val fileOutputStream = openFileOutput("config.txt", Context.MODE_APPEND)
        fileOutputStream.write("$name\n$buttonText\n$textViewText\n".toByteArray())
        fileOutputStream.close()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPresetViewerLogic() {
        val presetViewerButton: Button = findViewById(R.id.loadPreset)
        presetViewerButton.setOnClickListener {

            val presetsLayout: LinearLayout = layoutInflater.inflate(
                R.layout.preset_selector, null
            ) as LinearLayout

            val presets = readOptions()
            getPresetsAlert(presets, presetsLayout).show()

        }
    }

    val dataLen = 3
    @RequiresApi(Build.VERSION_CODES.N)
    fun readOptions(): List<PresetsData>? {
        val f = this.baseContext.getFileStreamPath("config.txt")
        if (!f.exists()) return null

        val fileInputStream = openFileInput("config.txt")
        val inputStreamReader = InputStreamReader(fileInputStream);
        val bufferedReader = BufferedReader(inputStreamReader);

        val rawData: List<String> = bufferedReader.lines().collect(Collectors.toList())
        inputStreamReader.close()

        val data: MutableList<PresetsData> = mutableListOf()
        for ((i, s) in rawData.withIndex()) {
            if (i % dataLen == 0)
                data.add(Triple(s, rawData[i+1].toInt(), rawData[i+2].toInt()))
        }
        return data.toList()
    }

    private fun getPresetsAlert(
        presets: List<PresetsData>?, layout: LinearLayout
    ): AlertDialog {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a preset")

        val alert = builder.create()

        presets?.let {
            presets.forEach { (name, tempo, bpms) ->
                val btn = getButtonForPreset(name)
                btn.setOnClickListener {
                    Rhythm.shared.setMeter(tempo)
                    Rhythm.shared.setBpm(bpms)
                    alert.cancel()
                }
                layout.addView(btn)
            }
        } ?: run {
            alert.setTitle("There are not any presets")
        }

        alert.setView(layout)
        return alert
    }

    private fun getButtonForPreset(name: String): Button {
        val btn = Button(this)
        btn.id = View.generateViewId()
        btn.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btn.setText(name)
        btn.setBackgroundColor(0x000000)
        return btn
    }

    private fun setAboutAppLogic() {
        val aboutButton: Button = findViewById(R.id.aboutButton)
        aboutButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("About us")
            dialogBuilder.setMessage(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Fusce et tristique enim, ut consequat risus. " +
                "Pellentesque augue velit, ultricies tempus elit id, " +
                "consectetur viverra turpis. Mauris magna ante, " +
                "molestie eu enim non, placerat malesuada risus. "
            )
            dialogBuilder.show()
        }
    }
}

