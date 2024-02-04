package com.autsing.codedroid.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autsing.codedroid.R
import com.autsing.codedroid.ui.viewmodels.DEFAULT_IP
import com.autsing.codedroid.ui.viewmodels.DEFAULT_PORT
import com.autsing.codedroid.ui.viewmodels.MainViewModel

@Composable
fun ConfigScreen(
    vm: MainViewModel = viewModel(),
) {
    val uiState = vm.uiState

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = uiState.ip,
                    onValueChange = vm::handleChangeIp,
                    label = { Text(stringResource(R.string.label_ip)) },
                    placeholder = { Text(DEFAULT_IP) },
                    singleLine = true,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(256.dp),
                )
                TextField(
                    value = uiState.port,
                    onValueChange = vm::handleChangePort,
                    label = { Text(stringResource(R.string.label_port)) },
                    placeholder = { Text(DEFAULT_PORT) },
                    singleLine = true,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(256.dp),
                )
                Button(
                    onClick = vm::handleGotoCode,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(256.dp),
                ) {
                    Text(stringResource(R.string.label_enter))
                }
                uiState.maybeException?.let {
                    Text(
                        text = stringResource(R.string.label_error, it),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}
