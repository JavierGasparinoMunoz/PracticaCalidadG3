package com.example.metronome

interface UserDao {
    fun createUser(user: User): Long
    fun getUserById(id: Long): User?
    fun updateUser(user: User): Boolean
    fun deleteUser(id: Long): Boolean
}