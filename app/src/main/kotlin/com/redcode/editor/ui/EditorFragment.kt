package com.redcode.editor.ui

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.redcode.editor.databinding.FragmentEditorBinding
import com.redcode.editor.model.EditorFile
import com.redcode.editor.model.Language
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class EditorFragment : Fragment() {
    
    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var editorFile: EditorFile
    private lateinit var codeEditor: CodeEditor
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val path = it.getString(ARG_FILE_PATH) ?: "Untitled"
            val content = it.getString(ARG_FILE_CONTENT) ?: ""
            val languageName = it.getString(ARG_LANGUAGE) ?: Language.PLAIN_TEXT.name
            val language = Language.valueOf(languageName)
            
            editorFile = EditorFile(
                path = path,
                content = content,
                language = language
            )
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditor()
    }
    
    private fun setupEditor() {
        codeEditor = binding.codeEditor
        
        // Configure editor settings
        codeEditor.apply {
            setText(editorFile.content)
            typefaceText = Typeface.MONOSPACE
            setTextSize(14f)
            isLineNumberEnabled = true
            isWordwrap = false
            
            // Set language
            setEditorLanguage(EmptyLanguage())
            
            // Configure color scheme based on system theme
            val colorScheme = EditorColorScheme()
            setColorScheme(colorScheme)
            
            // Add text change listener
            subscribeEvent(io.github.rosemoe.sora.event.ContentChangeEvent::class.java) { _, _ ->
                editorFile.isDirty = true
                editorFile.content = text.toString()
            }
            
            // Add cursor change listener
            subscribeEvent(io.github.rosemoe.sora.event.SelectionChangeEvent::class.java) { event, _ ->
                editorFile.cursorLine = event.left.line
                editorFile.cursorColumn = event.left.column
                updateCursorPosition()
            }
        }
    }
    
    private fun updateCursorPosition() {
        // Notify MainActivity to update status bar
        (activity as? MainActivity)?.updateStatusBar(
            editorFile.cursorLine + 1,
            editorFile.cursorColumn + 1,
            editorFile.language.displayName
        )
    }
    
    fun saveFile(): String {
        return codeEditor.text.toString()
    }
    
    fun getFile(): EditorFile {
        editorFile.content = codeEditor.text.toString()
        return editorFile
    }
    
    fun isDirty(): Boolean = editorFile.isDirty
    
    fun markClean() {
        editorFile.isDirty = false
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_FILE_PATH = "file_path"
        private const val ARG_FILE_CONTENT = "file_content"
        private const val ARG_LANGUAGE = "language"
        
        fun newInstance(path: String, content: String, language: Language): EditorFragment {
            return EditorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILE_PATH, path)
                    putString(ARG_FILE_CONTENT, content)
                    putString(ARG_LANGUAGE, language.name)
                }
            }
        }
    }
}
