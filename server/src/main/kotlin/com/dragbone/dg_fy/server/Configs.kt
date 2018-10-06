package com.dragbone.dg_fy.server

enum class Configs {
    Vote, MuteDuration;
    companion object {
        fun fromStringOrNull(value: String): Configs? {
            return Configs.values().firstOrNull { it.name == value.capitalize() }
        }
    }
}