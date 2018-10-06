package com.dragbone.dg_fy.server.config

interface IConfigEntry {
    fun getValue(): String
    fun setValue(value: String)
    fun getHtmlInputType(): String
}