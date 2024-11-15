package com.example.proyekakhirpemrogramanmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainRegister()
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Main(){
    Column(
    ){
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.Red).weight(1f)
        ){
            Column(
                modifier = Modifier.fillMaxHeight().background(Color.Gray).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text("World")
                Text("Akasha")
            }
            Column(
                modifier = Modifier.fillMaxHeight().background(Color.Magenta).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text("Hello")
                Text("Dummy")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().background(Color.Cyan).weight(1f)
        ){
            Column(
                modifier =Modifier.background(Color.Blue).fillMaxHeight().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text("Hello")
                Text("Hero")
            }

            Column(
                modifier =Modifier.background(Color.Yellow).fillMaxHeight().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text("World")
                Text("Dead")
            }
        }
    }
}
