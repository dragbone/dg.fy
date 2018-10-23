package com.dragbone.dg_fy.server.user

class UserMapper {
    private val userIdentifier = UserIdentifier()
    private val userMap = mutableMapOf<String, String>()

    fun getUser(ip: String): String? {
        val cachedUser = userMap[ip]
        if (cachedUser != null) return cachedUser

        val newUser = userIdentifier.getUser(ip)
        if (newUser != null) userMap[ip] = newUser
        return newUser ?: ip
    }
}
