package com.adri.tts

import android.content.pm.PackageManager
import android.os.Build
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

        // permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    1
                )
            }
        }

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


    // Lista de idiomas soportados ordenados alfabéticamente
    private val supportedLocales = listOf(
        // Árabe y dialectos
        Locale("ar") to "العربية (Árabe estándar)",
        Locale("ar", "EG") to "عربي (Egipto)",
        Locale("ar", "SA") to "عربي (Arabia Saudita)",
        
        // Bengalí
        Locale("bn", "IN") to "বাংলা (Bengalí)",
        
        // Catalán y variantes
        Locale("ca") to "Català (Estàndard)",
        Locale("ca", "AD") to "Català (Andorra)",
        Locale("ca", "ES") to "Català (Espanya)",
        Locale("ca", "FR") to "Català (França)",
        Locale("ca", "IT") to "Català (Itàlia)",
        
        // Chino y dialectos
        Locale.SIMPLIFIED_CHINESE to "中文 (简体) - Chino Mandarín",
        Locale.TRADITIONAL_CHINESE to "中文 (繁體) - Chino Tradicional",
        Locale("yue", "HK") to "粵語 (香港) - Cantonés",
        
        // Coreano
        Locale.KOREAN to "한국어 (Coreano)",
        Locale.KOREA to "한국어 (Corea del Sur)",
        
        // Danés
        Locale("da", "DK") to "Dansk (Danés)",
        
        // Español y dialectos
        Locale("es", "AR") to "Español (Argentina)",
        Locale("es", "CO") to "Español (Colombia)",
        Locale("es", "ES") to "Español (España - Castellano)",
        Locale("es", "MX") to "Español (México)",
        
        // Finés
        Locale("fi", "FI") to "Suomi (Finlandés)",
        
        // Francés y dialectos
        Locale.FRENCH to "Français (Francés - Estándar)",
        Locale.FRANCE to "Français (Francia)",
        Locale("fr", "BE") to "Français (Bélgica)",
        Locale("fr", "CA") to "Français (Canadá)",
        
        // Griego
        Locale("el", "GR") to "Ελληνικά (Griego)",
        
        // Hindi
        Locale("hi", "IN") to "हिन्दी (Hindi)",
        
        // Indonesio
        Locale("id") to "Bahasa Indonesia (Indonesio)",
        
        // Inglés y dialectos
        Locale.ENGLISH to "English (Inglés - EE.UU.)",
        Locale.US to "English (Estados Unidos)",
        Locale.UK to "English (Reino Unido)",
        Locale("en", "AU") to "English (Australia)",
        Locale("en", "CA") to "English (Canadá)",
        Locale("en", "IN") to "English (India)",
        
        // Italiano
        Locale.ITALIAN to "Italiano (Italia)",
        Locale.ITALY to "Italiano (Italia)",
        
        // Japonés
        Locale.JAPANESE to "日本語 (Japonés)",
        Locale.JAPAN to "日本語 (Japón)",
        
        // Malayo
        Locale("ms") to "Bahasa Melayu (Malayo)",
        
        // Neerlandés
        Locale("nl") to "Nederlands (Neerlandés)",
        Locale("nl", "BE") to "Nederlands (Bélgica)",
        
        // Noruego
        Locale("no", "NO") to "Norsk (Noruego - Bokmål)",
        Locale("nn", "NO") to "Norsk nynorsk (Noruego - Nynorsk)",
        
        // Polaco
        Locale("pl") to "Polski (Polaco)",
        
        // Portugués y dialectos
        Locale("pt", "BR") to "Português (Brasil)",
        Locale("pt", "PT") to "Português (Portugal)",
        
        // Rumano
        Locale("ro", "RO") to "Română (Rumano)",
        
        // Ruso
        Locale("ru", "RU") to "Русский (Ruso)",
        
        // Sueco
        Locale("sv", "SE") to "Svenska (Sueco)",
        
        // Tailandés
        Locale("th") to "ไทย (Tailandés)",
        
        // Turco
        Locale("tr") to "Türkçe (Turco)",
        
        // Vietnamita
        Locale("vi") to "Tiếng Việt (Vietnamita)"
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