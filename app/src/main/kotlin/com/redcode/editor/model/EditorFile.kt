package com.redcode.editor.model

data class EditorFile(
    var path: String,
    var content: String = "",
    var language: Language = Language.PLAIN_TEXT,
    var isDirty: Boolean = false,
    var cursorLine: Int = 0,
    var cursorColumn: Int = 0
)

enum class Language(val displayName: String, val extension: String) {
    PYTHON("Python", "py"),
    JAVASCRIPT("JavaScript", "js"),
    TYPESCRIPT("TypeScript", "ts"),
    HTML("HTML", "html"),
    CSS("CSS", "css"),
    PLAIN_TEXT("Plain Text", "txt");
    
    companion object {
        fun fromExtension(extension: String): Language {
            return values().find { 
                it.extension.equals(extension, ignoreCase = true) 
            } ?: PLAIN_TEXT
        }
        
        fun fromFileName(fileName: String): Language {
            val ext = fileName.substringAfterLast('.', "")
            return fromExtension(ext)
        }
    }
}
