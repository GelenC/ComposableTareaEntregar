package com.example.composabletareaentregar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope

class MainViewModel(val usuarioDao: UsuarioDao, val coroutineScope: CoroutineScope): ViewModel() {
}