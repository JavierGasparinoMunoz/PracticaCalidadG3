package com.example.metronome

class UserService(private val userDao: UserDao) {
    fun createUser(user: User): Long {
        return userDao.createUser(user)
    }

    fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }

    fun updateUser(user: User): Boolean {
        return userDao.updateUser(user)
    }

    fun deleteUser(id: Long): Boolean {
        return userDao.deleteUser(id)
    }
}
