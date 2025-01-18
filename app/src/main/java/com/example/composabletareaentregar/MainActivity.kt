@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composabletareaentregar

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.composabletareaentregar.Database.AppDatabase
import com.example.composabletareaentregar.Database.Usuario
import com.example.composabletareaentregar.Database.UsuarioDao
import com.example.composabletareaentregar.Database.UsuarioRepository
import com.example.composabletareaentregar.Retrofit.APIService
import com.example.composabletareaentregar.Retrofit.DogResponse
import com.example.composabletareaentregar.ui.theme.ComposableTareaEntregarTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

/*CAMBIOS A REALIZAR:
* Que se borren los campos de registro e inicio de sesión al terminar
* Mostrar mensaje cuando la busqueda no de resultados y eliminar las fotos anteriores
* */

class MainActivity : ComponentActivity() {
 //Creo el repositorio
    private lateinit var userRepository: UsuarioRepository
    private var isWorkerRunning= false

    // Actualizar interfaz en el hilo principal
    private val handler = Handler(Looper.getMainLooper())

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
                        startDestination = "inicio",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("inicio") {
                            Inicio(navController)
                        }
                        composable("iniciar_sesion") {
                            IniciarSesion(userRepository, navController, usuarioDao)
                        }
                        composable("registro") {
                            Registro(navController, usuarioDao, userRepository)
                        }
                        composable("consultar") {
                           Consultar(navController)
                        }
                    }
                }
            }
        }
    }
//Función para poner en marcha el hilo de la notificación
    fun iniciarHilo(context:Context) {
        if (!isWorkerRunning) {
            isWorkerRunning = true
            handler.post(object : Runnable {
                override fun run() {
                    Log.d("Consultar", "Ejecutando el ciclo del Handler para mostrar el Toast.")
                    avisoMessage("¡Puedes solicitar información en la API!", context)
                    // Repetir cada medio segundo
                    handler.postDelayed(this, 3000)
                }
            })
        }
    }

//Función para detener el hilo de la notificación
    fun detenerHilo(){
        if (isWorkerRunning) {
            isWorkerRunning = false
            handler.removeCallbacksAndMessages(null)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        detenerHilo()
    }
}


//Vista inicial de portada: My Dog API... (Se puede navegar a la vista de Iniciar sesión y salir de la app)
@Composable
fun Inicio(navController: NavController?=null) {

    //Imagen para que cubra el fondo
    val imagePainter = painterResource(id = R.drawable.inicio)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter,
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.padding(100.dp))
            Text(text = "My Dog API", fontSize = 50.sp, fontFamily = FontFamily.Serif)
            Spacer(modifier = Modifier.padding(10.dp))
            Button(
                onClick = {
                    navController?.navigate("iniciar_sesion")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                Text("Entrar", fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
//Cerrar la app si se presiona volver
    BackHandler {
        if (navController != null) {
            (navController.context as? ComponentActivity)?.finish()
        }
    }
}

/*Utilizo el ? = null para poder visualizarlo en la preview
* Vista de Inicio de sesion (ingresar usuario y correo registrados en la bd*/

@Composable
fun IniciarSesion(userRepository: UsuarioRepository? = null, navController: NavController?=null, usuarioDao: UsuarioDao? = null) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    LaunchedEffect(Unit) {
        activity?.detenerHilo()
    }
// Obtengo el CoroutineScope del Composable para ejecutar la corrutina
    val coroutineScope = rememberCoroutineScope()

// Estado para almacenar valores de usuario y correo
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    val imagePainter = painterResource(id = R.drawable.ini)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter,
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
//Añado los elementos
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(100.dp))
        Text(text = "INICIAR SESIÓN", fontSize = 20.sp, fontFamily = FontFamily.Serif)
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
                usuario = usuario.lowercase()
                correo = correo.lowercase()

                if(usuario.isEmpty() || correo.isEmpty()){
                    Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                }else{

//Validar que el correo tenga una sintaxis válida
                    if(verificarCorreo(correo)){
                        Toast.makeText(context, "El correo introducido no tiene una sintaxis válida", Toast.LENGTH_SHORT).show()
                    }else{
                        val inicioUsuario = Usuario(usuario, correo, 0)
                        if(userRepository!=null && navController!=null && usuarioDao!=null)
                            verificarUsuario(userRepository, coroutineScope, navController, usuarioDao,inicioUsuario, context)
                    }
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
    BackHandler {
        if (navController != null) {
            (navController.context as? ComponentActivity)?.finish()
        }
    }
}

//Función que verifica los carácteres que debe contener el correo
private fun verificarCorreo(correo:String):Boolean{
    return (!correo.contains("@gmail.com") && !correo.contains("@gmail.es") && !correo.contains("@hotmail.com")
            && !correo.contains("@hotmail.es") && !correo.contains("@yahoo.com") && !correo.contains("@yahoo.es")
            && !correo.contains("@outlook.com") && !correo.contains("@outlook.es") || correo.contains(" "))
}

//Función que verifica si el usuario está registrado en la base de datos
private fun verificarUsuario(userRepository: UsuarioRepository, coroutineScope:CoroutineScope, navController:NavController, usuarioDao: UsuarioDao, usuario: Usuario, context: Context){
 //Ejecuto la corrutina para llamar a la función "verificarUsuario"
    coroutineScope.launch {
        val usuarioExiste = userRepository.verificarUsuario(usuario.usuario)
//SI el usuario no existe vamos a la pantalla de Registro
        if(usuarioExiste == null){
            navController.navigate("registro")
        }else{
            if(usuarioExiste.correo == usuario.correo){
                usuarioDao.updateNumInicios(usuario.usuario)
                usuarioDao.updateFecha(usuario.usuario, LocalDateTime.now())
                navController.navigate("consultar")
            }else{
                Toast.makeText(context, "El correo que ingresó no coincide con el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

//Vista de registro, ingresar un correo y un usuario no existente en la bd
@Composable
fun Registro(navController: NavController?=null, usuarioDao: UsuarioDao?=null, userRepository: UsuarioRepository){

    val coroutineScope = rememberCoroutineScope()

// Estado para almacenar valores de usuario y correo
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    val context = LocalContext.current

    val imagePainter = painterResource(id = R.drawable.registrar)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter,
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
//Mostramos los elementos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(100.dp))
            Text(
                "¡Regístrate para poder continuar!",
                fontSize = 20.sp,
                modifier = Modifier.padding(40.dp)
            )
            TextField(
                value = usuario,
                onValueChange = { newText -> usuario = newText },
                placeholder = { Text("Usuario") },
                singleLine = true
            )
            TextField(
                value = correo,
                onValueChange = { newText -> correo = newText },
                placeholder = { Text("Correo") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
//Me creo el usuario y llamo a la función para guardarlo
                    val usuarioNuevo = Usuario(usuario, correo, 0)

                    if (usuarioDao != null && navController != null) {
                        registrarUsuario(
                            usuarioDao,
                            usuarioNuevo,
                            coroutineScope,
                            context,
                            userRepository,
                            navController
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("REGISTRARSE")
            }
            Spacer(modifier = Modifier.padding(50.dp))
            Button(
                onClick = {
                    navController?.navigate("iniciar_sesion")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                Text("VOLVER ATRÁS")
            }
        }
    }
    BackHandler {
        navController?.navigate("iniciar_sesion")
    }
}

//Funcion que registra a un usuario en la bd siempre y cuando cumpla con las condiciones válidas
private fun registrarUsuario(usuarioDao: UsuarioDao, usuario: Usuario, coroutineScope:CoroutineScope, context: Context, userRepository: UsuarioRepository, navController: NavController){
    coroutineScope.launch {
//Verificar que la sintaxis es válida
        if(verificarCorreo(usuario.correo)){
            Toast.makeText(context, "El correo introducido no tiene una sintaxis válida", Toast.LENGTH_SHORT).show()
        }else{
//Verificar que no sea un usuario existente
        val usuarioExiste = userRepository.verificarUsuario(usuario.usuario)
        if(usuarioExiste != null){
            Toast.makeText(context, "El usuario ingresado ya existe", Toast.LENGTH_SHORT).show()
        }else{
//Verifica que el correo no pertenece a otro usuario
            val correoExiste = userRepository.verificarCorreo(usuario.correo, usuario.usuario)
            if(correoExiste!=null){
                Toast.makeText(context, "El correo ingresado ya pertenece a otro usuario", Toast.LENGTH_SHORT).show()
            }else{
  // Insertar el usuario en la base de datos
                usuarioDao.insert(usuario)
                Toast.makeText(context, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("iniciar_sesion")
            }
        }
    }
  }
}

//Vista consultar donde se mostrará la notificación del handler y los resultados de la busqueda en la API
@Composable
fun Consultar(navController: NavController? = null) {
    val context = LocalContext.current
    var perroRaza by remember { mutableStateOf("") }

    val activity = context as? MainActivity
    val listState = rememberLazyListState()
    // Lista de datos para mostrar en la LazyColumn
    val dogImages = remember { mutableStateListOf<String>() }

    // Método para buscar las fotos de un perro por su raza
    fun buscarPorNombre() {
        CoroutineScope(Dispatchers.IO).launch {
            val perroMin = perroRaza.lowercase()
            val solicitar: Response<DogResponse> = retornarRetrofit().create(APIService::class.java).getPerrosRaza("${perroMin}/images")
            val perro: DogResponse? = solicitar.body()

            if (solicitar.isSuccessful && perro != null) {
                Log.d("Consultar", "Datos recibidos: ${perro.message}")
                withContext(Dispatchers.Main) {
                    dogImages.clear()
                    dogImages.addAll(perro.message)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al obtener datos de la API", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // Iniciar el ciclo del Handler
    LaunchedEffect(Unit) {
        activity?.iniciarHilo(context)
    }

    // Detener el Handler al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            activity?.detenerHilo()
        }
    }

    val imagePainter = painterResource(id = R.drawable.menu2)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter,
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Composable UI
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(79.dp))
            TextField(
                value = perroRaza,
                onValueChange = { newText -> perroRaza = newText },
                placeholder = { Text("Raza de perro") },
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = {
                    val activity = context as? MainActivity
                    activity?.detenerHilo()
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.scrollToItem(0)
                    }
                    buscarPorNombre()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(230.dp)
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                Text("CONSULTAR CON LA API")
            }
            Spacer(modifier = Modifier.padding(2.dp))
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(510.dp)
                    .padding(16.dp)
            ) {
                items(dogImages) { imageUrl ->
                    DogImageItem(imageUrl)
                }
            }
            Row {
                Button(
                    onClick = {
                        navController?.navigate("iniciar_sesion")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(145.dp)
                        .height(70.dp),
                    elevation = ButtonDefaults.buttonElevation(10.dp)
                ) {
                    Text("VOLVER ATRÁS")
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    onClick = {
                        if (navController != null) {
                            (navController.context as? ComponentActivity)?.finish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(70.dp),
                    elevation = ButtonDefaults.buttonElevation(10.dp)
                ) {
                    Text("SALIR")
                }
            }
        }
    }
    BackHandler {
        navController?.navigate("iniciar_sesion")
    }
}

// Función para mostrar el aviso de consulta
private fun avisoMessage(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


//Metodo para instanciar el objeto Retrofit
private fun retornarRetrofit():Retrofit{
    return Retrofit.Builder().baseUrl("https://dog.ceo/api/breed/").addConverterFactory(GsonConverterFactory.create()).build()
}

//Función para mostrar los resultados de la consulta en el lazy column
@Composable
fun DogImageItem(imageUrl: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Dog image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Permite que la altura se ajuste automáticamente
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Fit // Ajusta la imagen sin cortarla
        )
    }
}


@Preview
@Composable
fun Visualizacion(){
    //IniciarSesion()
    //Consultar()
    //Registro()
}