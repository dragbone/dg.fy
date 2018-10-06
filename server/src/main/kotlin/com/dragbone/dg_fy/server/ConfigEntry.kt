package com.dragbone.dg_fy.server

interface IConfigEntry {
    fun getValue(): String
    fun setValue(value: String)
    fun getHtmlInputType(): String
}

data class ConfigEntry(val name:String, val type: String, val value: String)

class IntConfigEntry(var value: Int) : IConfigEntry {
    override fun getValue(): String = value.toString()

    override fun setValue(value: String) {
        this.value = value.toInt()
    }

    override fun getHtmlInputType(): String = "number"
}

class BoolConfigEntry(var value: Boolean) : IConfigEntry {
    override fun getValue(): String = value.toString()

    override fun setValue(value: String) {
        this.value = value.toBoolean()
    }

    override fun getHtmlInputType(): String = "checkbox"
}