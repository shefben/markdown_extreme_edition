package com.mycompany.markdownproject.template

class TomlTemplateStripper : TemplateStripper {
    override fun strip(content: String): String {
        return content.lines().joinToString("\n") { line ->
            val idx = line.indexOf('=')
            if (idx == -1) line else {
                val key = line.substring(0, idx).trimEnd()
                val value = line.substring(idx + 1).trim()
                val newValue = if (value.startsWith('[')) "[]" else "\"\""
                "$key = $newValue"
            }
        }
    }
}
