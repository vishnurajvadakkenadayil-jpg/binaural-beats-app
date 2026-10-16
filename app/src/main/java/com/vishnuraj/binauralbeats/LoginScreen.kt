package com.vishnuraj.binauralbeats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onAuthenticated: (FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Binaural Beats", fontSize = 28.sp)
        Text(
            text = "Sign in to continue",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
        )

        errorMessage?.let { msg ->
            Text(text = msg, fontSize = 13.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = {
                errorMessage = null
                isLoading = true
                scope.launch {
                    val result = authManager.signInWithGoogle(context)
                    isLoading = false
                    result.fold(
                        onSuccess = { onAuthenticated(it) },
                        onFailure = { errorMessage = it.message ?: "Sign-in failed. Try again." }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Sign in with Google")
            }
        }
    }
}
