package com.example.composabletareaentregar.Database
/*capa intermedia entre el UsuarioDao y las clases que usan datos,*/

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun verificarUsuario(nombrebuscado:String): Usuario {
        return usuarioDao.verificarUsuario("%$nombrebuscado%")
    }

    suspend fun verificarCorreo(correo:String, usuario:String): Usuario {
        return usuarioDao.verificarCorreo("%$correo%", "%$usuario%")
    }
}