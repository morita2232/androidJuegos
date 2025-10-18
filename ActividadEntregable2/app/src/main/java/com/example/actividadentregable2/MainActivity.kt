package com.example.actividadentregable2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

enum class Screen {
    MENU,
    TRES_EN_RAYA,
    CRASH_GAME
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.MENU) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(60) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (timeLeft > 0 && currentScreen != Screen.MENU) {
                timeLeft--
            }
        }
    }

    if (timeLeft <= 0 && currentScreen != Screen.MENU) {
        EndScreen(score = score) {
            timeLeft = 60
            score = 0
            currentScreen = Screen.MENU
        }
        return
    }

    when (currentScreen) {
        Screen.MENU -> MainMenu(
            score = score,
            onSelectTicTacToe = { currentScreen = Screen.TRES_EN_RAYA },
            onSelectCrashGame = { currentScreen = Screen.CRASH_GAME }
        )

        Screen.TRES_EN_RAYA -> GameScreenWrapper(
            title = "Tres en Raya",
            score = score,
            timeLeft = timeLeft,
            onBack = { currentScreen = Screen.MENU },
            onSwitchGame = { currentScreen = Screen.CRASH_GAME }
        ) {
            TicTacToeSimple(onScoreChange = { score += it })
        }

        Screen.CRASH_GAME -> GameScreenWrapper(
            title = "Crash Game",
            score = score,
            timeLeft = timeLeft,
            onBack = { currentScreen = Screen.MENU },
            onSwitchGame = { currentScreen = Screen.TRES_EN_RAYA }
        ) {
            CrashGame(currentScore = score, onScoreChange = { score = it })
        }
    }
}

@Composable
fun MainMenu(score: Int, onSelectTicTacToe: () -> Unit, onSelectCrashGame: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🎮 BIENVENIDO A ESTA MARAVILLOSA EXPERIENCIA 🎮",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = onSelectTicTacToe) { Text("Jugar Tres en Raya") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSelectCrashGame) { Text("Jugar Crash Game") }
        Spacer(modifier = Modifier.height(30.dp))
        Text("⭐ Puntuación total: $score", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun TopBar(
    title: String,
    score: Int,
    timeLeft: Int,
    onBack: () -> Unit,
    onSwitchGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(32.dp)
            ) { Text("← Menú", fontSize = MaterialTheme.typography.labelSmall.fontSize) }

            Text(
                title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onSwitchGame,
                modifier = Modifier.height(32.dp)
            ) { Text("Cambiar", fontSize = MaterialTheme.typography.labelSmall.fontSize) }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "⏳ Tiempo: $timeLeft  |  ⭐ Puntos: $score",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun GameScreenWrapper(
    title: String,
    score: Int,
    timeLeft: Int,
    onBack: () -> Unit,
    onSwitchGame: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title, score, timeLeft, onBack, onSwitchGame)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
fun TicTacToeSimple(onScoreChange: (Int) -> Unit) {
    var board by remember { mutableStateOf(Array(9) { "" }) }
    var playerTurn by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("Tu turno (X)") }
    var gameOver by remember { mutableStateOf(false) }

    fun checkWinner(b: Array<String>): String? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (line in lines) {
            val (a, b1, c) = line
            if (b[a] != "" && b[a] == b[b1] && b[a] == b[c]) return b[a]
        }
        return null
    }

    fun aiMove() {
        if (gameOver) return
        val emptySpots = board.mapIndexed { i, v -> if (v == "") i else null }.filterNotNull()
        if (emptySpots.isNotEmpty()) {
            val move = emptySpots.random()
            board = board.copyOf().also { it[move] = "O" }
            val winner = checkWinner(board)
            if (winner != null) {
                message = "¡Ganó $winner!"
                onScoreChange(-100)
                gameOver = true
            } else if (board.all { it != "" }) {
                message = "Empate!"
                gameOver = true
            } else {
                message = "Tu turno (X)"
                playerTurn = true
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        Text(text = message, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    val index = i * 3 + j
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                when (board[index]) {
                                    "X" -> MaterialTheme.colorScheme.tertiaryContainer
                                    "O" -> MaterialTheme.colorScheme.errorContainer
                                    else -> Color.LightGray
                                }
                            )
                            .clickable(enabled = playerTurn && !gameOver && board[index] == "") {
                                board = board.copyOf().also { it[index] = "X" }
                                val winner = checkWinner(board)
                                if (winner != null) {
                                    message = "¡Ganaste!"
                                    gameOver = true
                                    onScoreChange(100)
                                } else if (board.all { it != "" }) {
                                    message = "Empate!"
                                    gameOver = true
                                    onScoreChange(50)
                                } else {
                                    message = "Turno de la IA (O)"
                                    playerTurn = false
                                    aiMove()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            board[index],
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            board = Array(9) { "" }
            playerTurn = true
            message = "Tu turno (X)"
            gameOver = false
        }) {
            Text("Reiniciar")
        }
    }
}

@Composable
fun CrashGame(currentScore: Int, onScoreChange: (Int) -> Unit) {
    var multiplier by remember { mutableStateOf(1.0f) }
    var crashed by remember { mutableStateOf(false) }
    var betting by remember { mutableStateOf(false) }
    var playerScore by remember { mutableStateOf(currentScore) }
    var statusMessage by remember { mutableStateOf("Presiona 'Apostar' para comenzar") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("💥 Crash Game 💥", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Multiplicador: x${"%.2f".format(multiplier)}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Saldo actual: $playerScore puntos", color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))
        Text(statusMessage)

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (!betting) {
                    if (playerScore >= 5) {
                        playerScore -= 5
                        betting = true
                        crashed = false
                        multiplier = 1.0f
                        statusMessage = "¡Apuesta en marcha!"
                    } else {
                        statusMessage = "No tienes suficientes puntos (mínimo 5)."
                    }
                } else {
                    betting = false
                    val ganancia = (5 * multiplier).toInt()
                    playerScore += ganancia
                    onScoreChange(playerScore)
                    statusMessage = "¡Retiraste con x${"%.2f".format(multiplier)}! +$ganancia puntos"
                }
            }
        ) {
            Text(if (!betting) "Apostar (5 puntos)" else "Retirar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (betting && !crashed) {
            LaunchedEffect(multiplier) {
                delay(500)
                multiplier += 0.3f
                if (Random.nextFloat() < 0.12f) {
                    crashed = true
                    betting = false
                    statusMessage = "💥 ¡Crash! Perdiste tu apuesta"
                }
            }
        }
    }
}

@Composable
fun EndScreen(score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🎉 ¡Felicidades! 🎉",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Has conseguido $score puntos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart) {
            Text("Volver al Menú")
        }
    }
}



/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
      // MainMenu(showScreen = false, onStartGame = onStartGame())
         //TicTacToeSimple()
    }
}*/