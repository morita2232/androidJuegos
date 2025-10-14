package com.example.actividadentregable2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showScreen by remember { mutableStateOf(false) }


                    MainMenu(showScreen = showScreen,  onStartGame = { showScreen = true })
                }
            }
        }
    }
}

@Composable
fun MainMenu(showScreen : Boolean,onStartGame: () -> Unit, modifier: Modifier = Modifier) {

Column() {
    Spacer(modifier = Modifier.height(100.dp))
    Text(
        text = "BIENVENIDO A ESTA MARAVILLOSA EXPERIENCIA",
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(100.dp))
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
    if (showScreen) {
        TicTacToeSimple()
    } else {
        Button(onClick = onStartGame) {
            Text("Jugar")
        }
    }
}
}
}

@Composable
fun TicTacToeSimple() {
    var board by remember { mutableStateOf(Array(9) { "" }) }
    var playerTurn by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("Tu turno (X)") }
    var gameOver by remember { mutableStateOf(false) }

    fun checkWinner(b: Array<String>): String? {
        val lines = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )
        for (line in lines) {
            val (a, b1, c) = line
            if (board[a] != "" && board[a] == board[b1] && board[a] == board[c]) {
                return board[a]
            }
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

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(color = MaterialTheme.colorScheme.primaryContainer)) {
        Text(text = message, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(16.dp))
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    val index = i * 3 + j
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray)
                            .clickable(enabled = playerTurn && !gameOver && board[index] == "") {
                                if (!gameOver && board[index] == "") {
                                    board = board
                                        .copyOf()
                                        .also { it[index] = "X" }
                                    val winner = checkWinner(board)
                                    if (winner != null) {
                                        message = "¡Ganaste!"
                                        gameOver = true
                                    } else if (board.all { it != "" }) {
                                        message = "Empate!"
                                        gameOver = true
                                    } else {
                                        message = "Turno de la IA (O)"
                                        playerTurn = false
                                        aiMove()
                                    }
                                }
                            }
                            .background(color = MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = board[index], color = MaterialTheme.colorScheme.onTertiaryContainer, style = MaterialTheme.typography.headlineMedium)
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
        }, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = Color.White)) {
            Text("Reiniciar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
      // MainMenu(showScreen = false, onStartGame = onStartGame())
         //TicTacToeSimple()
    }
}