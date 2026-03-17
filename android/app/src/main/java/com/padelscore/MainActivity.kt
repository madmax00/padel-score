package com.padelscore

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SAPService.viewModel = viewModel
        startService(Intent(this, SAPService::class.java))
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                PadelScoreScreen(viewModel)
            }
        }
    }
}

@Composable
fun PadelScoreScreen(viewModel: ScoreViewModel) {
    val match by viewModel.match.collectAsState()
    val (myPts, oppPts) = match.displayPoints()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text("PADEL SCORE", color = Color(0xFF00D4FF), fontSize = 18.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 4.sp)

        Spacer(Modifier.height(32.dp))

        // Sets row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("NOI", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("SET", color = Color(0xFF888888), fontSize = 12.sp)
            Text("LORO", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        // Completed sets
        match.sets.forEachIndexed { i, set ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(set.myGames.toString(), color = Color(0xFFAAAAAA), fontSize = 16.sp)
                Text("Set ${i + 1}", color = Color(0xFF666666), fontSize = 12.sp)
                Text(set.oppGames.toString(), color = Color(0xFFAAAAAA), fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Current set (big)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SET ${match.sets.size + 1}", color = Color(0xFF00D4FF), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(match.currentSet.myGames.toString(), color = Color.White,
                        fontSize = 72.sp, fontWeight = FontWeight.Bold)
                    Text("-", color = Color(0xFF666666), fontSize = 72.sp)
                    Text(match.currentSet.oppGames.toString(), color = Color.White,
                        fontSize = 72.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Current game points
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3460))
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val label = if (match.isTiebreak) "TIEBREAK" else "GIOCO"
                Text(label, color = Color(0xFF00D4FF), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(myPts, color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Bold)
                    Text("-", color = Color(0xFF666666), fontSize = 56.sp)
                    Text(oppPts, color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Manual buttons (backup for when watch is not connected)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.addMyPoint() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))
            ) { Text("+1 NOI", color = Color.Black, fontWeight = FontWeight.Bold) }

            Button(
                onClick = { viewModel.addOppPoint() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
            ) { Text("+1 LORO", color = Color.White, fontWeight = FontWeight.Bold) }
        }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.undo() },
                modifier = Modifier.weight(1f)
            ) { Text("ANNULLA", color = Color(0xFFAAAAAA)) }

            OutlinedButton(
                onClick = { viewModel.reset() },
                modifier = Modifier.weight(1f)
            ) { Text("RESET", color = Color(0xFFFF6B6B)) }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Watch: 1 tap = NOI  •  2 tap = LORO  •  3 tap = ANNULLA",
            color = Color(0xFF555555), fontSize = 11.sp, textAlign = TextAlign.Center
        )
    }
}
