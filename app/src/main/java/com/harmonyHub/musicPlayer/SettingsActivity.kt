package com.harmonyHub.musicPlayer

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.harmonyHub.musicPlayer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        binding.coolBlueTheme.setOnClickListener { saveTheme(0) }
        binding.coolBlackTheme.setOnClickListener { saveTheme(1) }
        when(MainActivity.themeIndex) {
            0 -> {
                binding.textViewBlue.setTypeface(null, Typeface.BOLD)
                binding.textViewBlue.setText("Blue theme (current theme)")
            }
            1 -> {
                binding.textViewBlack.setTypeface(null, Typeface.BOLD)
                binding.textViewBlack.setText("Black theme (current theme)")
            }
        }
//        binding.versionName.text = setVersionDetails()
//        binding.sortBtn.setOnClickListener {
//            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
//            var currentSort = MainActivity.sortOrder
//            val builder = MaterialAlertDialogBuilder(this)
//            builder.setTitle("Sorting")
//                .setPositiveButton("OK"){ _, _ ->
//                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
//                    editor.putInt("sortOrder", currentSort)
//                    editor.apply()
//                }
//                .setSingleChoiceItems(menuList, currentSort){ _,which->
//                    currentSort = which
//                }
//            val customDialog = builder.create()
//            customDialog.show()
//
//            setDialogBtnBackground(this, customDialog)
//        }
    }

    private fun saveTheme(index: Int){
        if(MainActivity.themeIndex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do you want to apply theme?")
                .setPositiveButton("Yes"){ _, _ ->
                    exitApplication()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }
    }
}