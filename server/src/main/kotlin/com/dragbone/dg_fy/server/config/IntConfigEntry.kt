package com.dragbone.dg_fy.server.config

class IntConfigEntry(var value: Int) : IConfigEntry {
    override fun getValue(): String = value.toString()

    override fun setValue(value: String) {
        this.value = value.toInt()
    }

    override fun getHtmlInputType(): String = "number"
}