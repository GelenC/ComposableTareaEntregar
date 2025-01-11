package com.example.composabletareaentregar

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.composabletareaentregar.ui.theme.ComposableTareaEntregarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*CAMBIOS A REALIZAR:
*Verificar la entrada de correos segun valores aceptables.
*Cambiar el aviso de inicio fallido con un text()
* */

class MainActivity : ComponentActivity() {
 //Creo el repositorio
    private lateinit var userRepository: UsuarioRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableTareaEntregarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

  //Creo mis variables para manejar mi base de datos
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "user_database").build()
                    val usuarioDao = db.usuarioDao()
                    userRepository = UsuarioRepository(usuarioDao)

  //Preparo el NavigationController (Se importa por defecto)
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "iniciar_sesion",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("iniciar_sesion") {
                            IniciarSesion(userRepository, navController, usuarioDao)
                        }
                        composable("registro") {
                            Registro(navController, usuarioDao)
                        }
                        composable("consultar") {
                           Consultar(navController)
                        }
                    }
                }
            }
        }
    }
}

//Utilizo el ? = null para poder visualizarlo en la preview
@Composable
fun IniciarSesion(userRepository: UsuarioRepository? = null, navController: NavController?=null, usuarioDao: UsuarioDao? = null) {

// Obtengo el CoroutineScope del Composable para ejecutar la corrutina
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
// Estado para almacenar valores de usuario y correo
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

//Añado los elementos
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(100.dp))
        Text(text = "INICIAR SESIÓN", fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(10.dp))
        TextField(
            value = usuario,
            onValueChange = { newText -> usuario = newText },
            placeholder = { Text("Usuario") },
            singleLine = true)
        Spacer(modifier = Modifier.padding(10.dp))
        TextField(
            value = correo,
            onValueChange = { newText -> correo = newText },
            placeholder = { Text("Correo") },
            singleLine = true)
        Spacer(modifier = Modifier.padding(10.dp))
        Button(
            onClick = {
                if(usuario.isEmpty() || correo.isEmpty()){
                    Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                }else{
                    val InicioUsuario = Usuario(usuario, correo, 0)
                    if(userRepository!=null && navController!=null && usuarioDao!=null)
                        VerificarUsuario(userRepository, coroutineScope, navController, InicioUsuario, context)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(10.dp)
        ) {
            Text("INICIAR SESIÓN", fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

private fun VerificarUsuario(userRepository: UsuarioRepository, coroutineScope:CoroutineScope, navController:NavController, usuario: Usuario, context: Context){
 //Ejecuto la corrutina para llamar a la función "verificarUsuario"
    coroutineScope.launch {
        val usuarioExiste = userRepository.verificarUsuario(usuario.usuario)

//SI el usuario no existe vamos a la pantalla de Registro
        if(usuarioExiste == null){
            navController.navigate("registro")
        }else{
            if(usuarioExiste.correo == usuario.correo){
                navController.navigate("consultar")
            }else{
                Toast.makeText(context, "El correo que ingresó no coincide con el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun Registro(navController: NavController?=null, usuarioDao: UsuarioDao?=null){

    val coroutineScope = rememberCoroutineScope()

// Estado para almacenar valores de usuario y correo
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    val context = LocalContext.current

//Mostramos los elementos
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(100.dp))
        Text("¡Regístrate para poder continuar!", fontSize = 20.sp, modifier = Modifier.padding(40.dp))
        TextField(
            value = usuario,
            onValueChange = { newText -> usuario = newText },
            placeholder = { Text("Usuario") },
            singleLine = true)
        TextField(
            value = correo,
            onValueChange = { newText -> correo = newText },
            placeholder = { Text("Correo") },
            singleLine = true)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
//Me creo el usuario y llamo a la función para guardarlo
            val usuarioNuevo = Usuario(usuario, correo, 0)
            if(usuarioDao!=null)
            RegistrarUsuario(usuarioDao, usuarioNuevo, coroutineScope, context)
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)) {
            Text("REGISTRARSE")
        }
        Spacer(modifier = Modifier.padding(100.dp))
        Button(onClick = {
            if (navController!=null)
                navController.navigate("iniciar_sesion")
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
            modifier = Modifier
                .width(150.dp)
                .height(40.dp),
            elevation = ButtonDefaults.buttonElevation(10.dp)) {
            Text("VOLVER ATRÁS")
        }
    }
}

private fun RegistrarUsuario(usuarioDao: UsuarioDao, usuario: Usuario, coroutineScope:CoroutineScope, context: Context){
    coroutineScope.launch {
// Insertar el usuario en la base de datos
        usuarioDao.insert(usuario)
        Toast.makeText(context, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()
    }
}
@Composable
fun Consultar(navController: NavController?=null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = {},colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
            modifier = Modifier
                .width(230.dp)
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(10.dp)) {
            Text("CONSULTAR CON LA API")
        }
        Spacer(modifier = Modifier.padding(300.dp))
        Button(onClick = {
            if (navController!=null)
            navController.navigate("iniciar_sesion")
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(10.dp)) {
            Text("VOLVER ATRÁS")
        }
    }
}

@Preview
@Composable
fun Visualizacion(){
    //IniciarSesion()
    //Consultar()
     Registro()
}