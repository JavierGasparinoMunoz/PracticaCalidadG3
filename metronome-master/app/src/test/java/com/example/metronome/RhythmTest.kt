package com.example.metronome

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class RhythmTest {

    @Mock
    lateinit var applicationContext: AppCompatActivity

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var beatCircles: List<MaterialButton>

    private lateinit var rhythm: Rhythm

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(sharedPreferences.getInt("bpm", 100)).thenReturn(100)
        `when`(sharedPreferences.getInt("meter", 4)).thenReturn(4)
        rhythm = Rhythm(applicationContext, sharedPreferences, beatCircles)
    }

    @Test
    fun testStart() {
        rhythm.start()
        assertFalse(rhythm.isPaused())
    }

    @Test
    fun testStop() {
        rhythm.stop()
        assertTrue(rhythm.isPaused())
    }

    @Test
    fun testSetMeter() {
        rhythm.setMeter(3)
        assertEquals(3, rhythm.getMeter())
        verify(sharedPreferences).edit().putInt("meter", 3)
        verify(sharedPreferences.edit(), times(1)).commit()
    }

    @Test
    fun testSetBpm() {
        rhythm.setBpm(120)
        assertEquals(120, rhythm.getBpm())
        verify(sharedPreferences).edit().putInt("bpm", 120)
        verify(sharedPreferences.edit(), times(1)).commit()
    }
}
