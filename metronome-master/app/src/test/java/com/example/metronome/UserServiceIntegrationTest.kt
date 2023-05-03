package com.example.metronome

import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

class UserServiceIntegrationTest {

    private val userDao = mock(UserDao::class.java)
    private val userService = UserService(userDao)

    @Test
    // Verificar que se crea un usuaro con éxito al crearlo y obtener su id
    fun `create user should call UserDao and return the created user id`() {
        val user = User(1L, "Pepito", "pepito22@gmail.com", "Dark")
        `when`(userDao.createUser(user)).thenReturn(1L)

        val result = userService.createUser(user)

        verify(userDao).createUser(user)
        assertEquals(1L, result)
    }

    @Test
    // Verificar que obtenemos el usuario deseado por su id
    fun `get user by id should call UserDao and return the user`() {
        val user = User(1L, "Pepito", "pepito22@gmail.com", "Dark")
        `when`(userDao.getUserById(1L)).thenReturn(user)

        val result = userService.getUserById(1L)

        verify(userDao).getUserById(1L)
        assertEquals(user, result)
    }

    @Test
    // Verificar que se actualiza un usuario
    fun `update user should call UserDao and return success`() {
        val user = User(1L, "Pepito", "pepito22@gmail.com", "Dark")
        `when`(userDao.updateUser(user)).thenReturn(true)

        val result = userService.updateUser(user)

        verify(userDao).updateUser(user)
        assertTrue(result)
    }

    @Test
    // Verificar que se elimina un usuario con éxito
    fun `delete user should call UserDao and return success`() {
        `when`(userDao.deleteUser(1L)).thenReturn(true)

        val result = userService.deleteUser(1L)

        verify(userDao).deleteUser(1L)
        assertTrue(result)
    }
}
