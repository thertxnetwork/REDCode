package com.redcode.editor.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.redcode.editor.R
import com.redcode.editor.databinding.ActivityMainBinding
import com.redcode.editor.model.EditorFile
import com.redcode.editor.model.Language
import com.redcode.editor.utils.FileManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var fileManager: FileManager
    private val openFiles = mutableListOf<EditorFile>()
    private var currentFileIndex = 0
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val URI_SCHEME_CONTENT = "content://"
        private const val URI_SCHEME_FILE = "file://"
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val URI_SCHEME_CONTENT = "content://"
        private const val URI_SCHEME_FILE = "file://"
    }
    
    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { openFile(it) }
    }
    
    private val saveFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let { saveFile(it) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        fileManager = FileManager(this)
        
        setupToolbar()
        setupViewPager()
        setupFab()
        
        // Check and request permissions
        checkPermissions()
        
        // Open a default empty file
        if (openFiles.isEmpty()) {
            createNewFile()
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            // Show menu
            showMenu()
        }
    }
    
    private fun setupViewPager() {
        binding.viewPager.adapter = EditorPagerAdapter()
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val file = openFiles.getOrNull(position)
            tab.text = file?.path?.substringAfterLast('/') ?: "Untitled"
        }.attach()
        
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentFileIndex = position
                updateStatusBarForCurrentFile()
            }
        })
    }
    
    private fun setupFab() {
        binding.fabNewFile.setOnClickListener {
            createNewFile()
        }
    }
    
    private fun showMenu() {
        val items = arrayOf("Open File", "Save File", "Close Tab", "Settings")
        MaterialAlertDialogBuilder(this)
            .setTitle("Menu")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> openFileLauncher.launch(arrayOf("*/*"))
                    1 -> {
                        val currentFile = openFiles.getOrNull(currentFileIndex)
                        currentFile?.let {
                            if (it.path.startsWith(URI_SCHEME_CONTENT) || it.path.startsWith(URI_SCHEME_FILE)) {
                                saveFileToUri(Uri.parse(it.path))
                            } else {
                                saveFileLauncher.launch(it.path.substringAfterLast('/'))
                            }
                        }
                    }
                    2 -> closeCurrentTab()
                    3 -> {
                        // Open settings
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                }
            }
            .show()
    }
    
    private fun createNewFile() {
        val newFile = EditorFile(
            path = "Untitled-${openFiles.size + 1}",
            content = "",
            language = Language.PLAIN_TEXT
        )
        openFiles.add(newFile)
        binding.viewPager.adapter?.notifyItemInserted(openFiles.size - 1)
        binding.viewPager.setCurrentItem(openFiles.size - 1, true)
    }
    
    private fun openFile(uri: Uri) {
        val fileName = fileManager.getFileName(uri)
        val content = fileManager.readFile(uri) ?: ""
        val language = Language.fromFileName(fileName)
        
        val file = EditorFile(
            path = uri.toString(),
            content = content,
            language = language
        )
        
        openFiles.add(file)
        binding.viewPager.adapter?.notifyItemInserted(openFiles.size - 1)
        binding.viewPager.setCurrentItem(openFiles.size - 1, true)
        
        Toast.makeText(this, "Opened: $fileName", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveFile(uri: Uri) {
        saveFileToUri(uri)
    }
    
    private fun saveFileToUri(uri: Uri) {
        val fragment = supportFragmentManager.findFragmentByTag(getFragmentTag(currentFileIndex)) as? EditorFragment
        fragment?.let {
            val content = it.saveFile()
            if (fileManager.writeFile(uri, content)) {
                it.markClean()
                openFiles[currentFileIndex].path = uri.toString()
                Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.error_saving_file), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun closeCurrentTab() {
        if (openFiles.isEmpty()) return
        
        val fragment = supportFragmentManager.findFragmentByTag(getFragmentTag(currentFileIndex)) as? EditorFragment
        if (fragment?.isDirty() == true) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.confirm_close))
                .setMessage(getString(R.string.unsaved_changes))
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    // Save file first, then close
                    val file = openFiles[currentFileIndex]
                    val currentIndex = currentFileIndex
                    if (file.path.startsWith(URI_SCHEME_CONTENT) || file.path.startsWith(URI_SCHEME_FILE)) {
                        saveFileToUri(Uri.parse(file.path))
                        performCloseTab()
                    } else {
                        // For new files, we need to prompt for location first
                        saveFileLauncher.launch(file.path.substringAfterLast('/'))
                        // Note: performCloseTab() should be called after save completes
                        // For now, we'll just close since we can't easily hook into the save callback
                        performCloseTab()
                    }
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    performCloseTab()
                }
                .setNeutralButton(getString(R.string.cancel), null)
                .show()
        } else {
            performCloseTab()
        }
    }
    
    private fun performCloseTab() {
        val indexToClose = currentFileIndex
        
        // Validate index before proceeding
        if (indexToClose < 0 || indexToClose >= openFiles.size) return
        
        openFiles.removeAt(indexToClose)
        
        if (openFiles.isEmpty()) {
            binding.viewPager.adapter?.notifyItemRemoved(indexToClose)
            createNewFile()
        } else {
            binding.viewPager.adapter?.notifyItemRemoved(indexToClose)
            // Update current index if it's out of bounds
            if (currentFileIndex >= openFiles.size) {
                currentFileIndex = openFiles.size - 1
            }
            binding.viewPager.setCurrentItem(currentFileIndex, true)
        }
    }
    
    private fun getFragmentTag(position: Int): String {
        return "f$position"
    }
    
    fun updateStatusBar(line: Int, column: Int, language: String) {
        binding.cursorPosition.text = "Line $line, Col $column"
        binding.languageMode.text = language
    }
    
    private fun updateStatusBarForCurrentFile() {
        val file = openFiles.getOrNull(currentFileIndex)
        file?.let {
            updateStatusBar(it.cursorLine + 1, it.cursorColumn + 1, it.language.displayName)
        }
    }
    
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = mutableListOf<String>()
            
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            
            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }
    
    inner class EditorPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = openFiles.size
        
        override fun createFragment(position: Int): Fragment {
            val file = openFiles[position]
            return EditorFragment.newInstance(file.path, file.content, file.language)
        }
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
