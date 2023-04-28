package com.example . metronome

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(Rhythm::class)
class RhythmTest {
    private var rhythm: Rhythm? = null
    @Before
    fun setUp() {
        rhythm = Mockito.mock(Rhythm::class.java)
    }

    @Test
    fun start() {
        rhythm?.start()
        assert(rhythm?.isPaused()==false)
    }

    @Test
    fun stop() {
        rhythm?.stop()
        assert(rhythm?.isPaused()==true)
    }

    fun setMeter(){
        rhythm?.setMeter(100)
        assert(rhythm?.getMeter()==100)
    }

    fun setBpm(){
        rhythm?.setBpm(100)
        assert(rhythm?.getMeter()==100)
    }

    fun increaseBpm() {
        rhythm?.setBpm(100)
        rhythm?.increaseBpm()
        assert(rhythm?.getBpm()==101)
    }

    fun decreaseBpm() {
        rhythm?.setBpm(100)
        rhythm?.decreaseBpm()
        assert(rhythm?.getBpm()==99)
    }
}

