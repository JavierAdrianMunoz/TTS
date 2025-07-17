package com.adri.tts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import java.util.*

// Extending MainActivity TextToSpeech.OnInitListener class
class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var btnSpeak: Button? = null
    private var etSpeak: EditText? = null
    private var spLanguage: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // spinner
        spLanguage = findViewById(R.id.sp_language)


        // view binding button and edit text
        btnSpeak = findViewById(R.id.btn_speak)
        etSpeak = findViewById(R.id.et_input)

        btnSpeak!!.isEnabled = false

        // TextToSpeech(Context: this, OnInitListener: this)
        tts = TextToSpeech(this, this)

        btnSpeak!!.setOnClickListener { speakOut() }

    }


    // List of supported TTS languages with their display names
    private val supportedLocales = listOf(
        Locale.ENGLISH to "English (US)",
        Locale.UK to "English (UK)",
        Locale.FRENCH to "French",
        Locale.FRANCE to "French (France)",
        Locale.GERMAN to "German",
        Locale.GERMANY to "German (Germany)",
        Locale.ITALIAN to "Italian",
        Locale.ITALY to "Italian (Italy)",
        Locale.JAPANESE to "Japanese",
        Locale.JAPAN to "Japanese (Japan)",
        Locale.KOREAN to "Korean",
        Locale.KOREA to "Korean (Korea)",
        Locale.CHINESE to "Chinese",
        Locale.SIMPLIFIED_CHINESE to "Chinese (Simplified)",
        Locale.TRADITIONAL_CHINESE to "Chinese (Traditional)",
        Locale("es", "ES") to "Spanish (Spain)",
        Locale("es", "MX") to "Spanish (Mexico)",
        Locale("pt", "BR") to "Portuguese (Brazil)",
        Locale("pt", "PT") to "Portuguese (Portugal)",
        Locale("ru", "RU") to "Russian",
        Locale("ar") to "Arabic",
        Locale("hi", "IN") to "Hindi",
        Locale("bn", "IN") to "Bengali",
        Locale("nl") to "Dutch",
        Locale("pl") to "Polish",
        Locale("tr") to "Turkish",
        Locale("th") to "Thai",
        Locale("vi") to "Vietnamese",
        Locale("id") to "Indonesian",
        Locale("ms") to "Malay"
    )

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Create a list of display names for the spinner
            val displayNames = supportedLocales.map { it.second }
            
            // Set up the adapter with display names
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                displayNames
            )
            
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spLanguage!!.adapter = adapter
            
            // Set default language to English (US)
            val defaultLocale = Locale.ENGLISH
            val defaultIndex = supportedLocales.indexOfFirst { it.first == defaultLocale }.takeIf { it >= 0 } ?: 0
            spLanguage!!.setSelection(defaultIndex)
            
            spLanguage!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLocale = supportedLocales[position].first
                    val result = tts!!.setLanguage(selectedLocale)

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported: ${selectedLocale.displayLanguage}")
                        btnSpeak!!.isEnabled = false
                    } else {
                        btnSpeak!!.isEnabled = etSpeak!!.text.isNotEmpty()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Set default language to English
                    val result = tts!!.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Default language (English) not supported!")
                        btnSpeak!!.isEnabled = false
                    } else {
                        btnSpeak!!.isEnabled = etSpeak!!.text.isNotEmpty()
                    }
                }
            }
        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }
    private fun speakOut() {
        val text = etSpeak!!.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}