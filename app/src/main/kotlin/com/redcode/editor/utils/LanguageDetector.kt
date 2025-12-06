package com.redcode.editor.utils

import com.redcode.editor.model.Language

object LanguageDetector {
    
    fun detectFromExtension(extension: String): Language {
        return when (extension.lowercase()) {
            "py" -> Language.PYTHON
            "js" -> Language.JAVASCRIPT
            "ts", "tsx" -> Language.TYPESCRIPT
            "html", "htm" -> Language.HTML
            "css", "scss", "sass", "less" -> Language.CSS
            else -> Language.PLAIN_TEXT
        }
    }
    
    fun detectFromFileName(fileName: String): Language {
        val extension = fileName.substringAfterLast('.', "")
        if (extension.isEmpty()) {
            // Check for files without extension
            return when (fileName.lowercase()) {
                "dockerfile" -> Language.PLAIN_TEXT
                "makefile" -> Language.PLAIN_TEXT
                "readme" -> Language.PLAIN_TEXT
                else -> Language.PLAIN_TEXT
            }
        }
        return detectFromExtension(extension)
    }
    
    fun detectFromContent(content: String, fileName: String = ""): Language {
        // First try filename
        if (fileName.isNotEmpty()) {
            val fromFile = detectFromFileName(fileName)
            if (fromFile != Language.PLAIN_TEXT) {
                return fromFile
            }
        }
        
        // Detect from shebang
        if (content.startsWith("#!")) {
            val firstLine = content.lines().firstOrNull() ?: ""
            return when {
                "python" in firstLine.lowercase() -> Language.PYTHON
                "node" in firstLine.lowercase() -> Language.JAVASCRIPT
                "bash" in firstLine.lowercase() || "sh" in firstLine.lowercase() -> Language.PLAIN_TEXT
                else -> Language.PLAIN_TEXT
            }
        }
        
        // Detect from content patterns
        return when {
            content.contains("<!DOCTYPE html", ignoreCase = true) || 
            content.contains("<html", ignoreCase = true) -> Language.HTML
            content.contains("def ") && content.contains("import ") -> Language.PYTHON
            content.contains("function ") || content.contains("const ") || 
            content.contains("let ") || content.contains("var ") -> Language.JAVASCRIPT
            content.contains("interface ") && content.contains(": ") -> Language.TYPESCRIPT
            content.contains("{") && content.contains("}") && 
            content.contains(":") && content.contains(";") -> Language.CSS
            else -> Language.PLAIN_TEXT
        }
    }
}
