package com.dragbone.dg_fy.server.config

class BoolConfigEntry(var value: Boolean) : IConfigEntry {
    override fun getValue(): String = value.toString()

    override fun setValue(value: String) {
        this.value = value.toBoolean()
    }

    override fun getHtmlInputType(): String = "checkbox"
}