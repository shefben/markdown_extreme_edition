package com.mycompany.markdownproject.template

import com.google.gson.*

class JsonTemplateStripper : TemplateStripper {
    private val gson = Gson()
    private val pretty = GsonBuilder().setPrettyPrinting().create()

    override fun strip(content: String): String {
        return try {
            val element = gson.fromJson(content, JsonElement::class.java)
            val stripped = stripElement(element)
            pretty.toJson(stripped).trim()
        } catch (e: Exception) {
            content
        }
    }

    private fun stripElement(el: JsonElement): JsonElement {
        return when {
            el.isJsonObject -> {
                val obj = JsonObject()
                for ((k, v) in el.asJsonObject.entrySet()) {
                    obj.add(k, stripElement(v))
                }
                obj
            }
            el.isJsonArray -> JsonArray()
            el.isJsonPrimitive -> {
                val p = el.asJsonPrimitive
                when {
                    p.isString -> JsonPrimitive("")
                    p.isNumber -> JsonPrimitive(0)
                    p.isBoolean -> JsonPrimitive(false)
                    else -> el
                }
            }
            else -> JsonNull.INSTANCE
        }
    }
}
