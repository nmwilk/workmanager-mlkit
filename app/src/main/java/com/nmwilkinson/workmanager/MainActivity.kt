package com.nmwilkinson.workmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmwilkinson.workmanager.ui.theme.WorkManagerTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (application as App).viewModel

        setContent {
            val strings by viewModel.stringsFlow.collectAsState()
            WorkManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(strings) {
                            Entry(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Entry(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        style = TextStyle(fontSize = 30.sp),
        modifier = modifier.padding(20.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkManagerTheme {
        Entry("Android")
    }
}