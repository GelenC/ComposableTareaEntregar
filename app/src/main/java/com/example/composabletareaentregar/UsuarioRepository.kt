package com.example.composabletareaentregar

class UsuarioRepository(private val usuarioDao:UsuarioDao) {
    suspend fun verificarUsuario(nombrebuscado:String):Usuario{
        return usuarioDao.verificarUsuario("%$nombrebuscado%")
    }
}