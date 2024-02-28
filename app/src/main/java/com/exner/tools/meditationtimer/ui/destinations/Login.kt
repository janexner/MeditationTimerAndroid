package com.exner.tools.meditationtimer.ui.destinations

import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.LoginViewModel
import com.exner.tools.meditationtimer.ui.findActivity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun Login(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val accountType = "net.fototimer"

    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility) Icons.Filled.Warning else Icons.Filled.Lock

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                },
                label = { Text(text = "Login") },
                singleLine = true,
                modifier = Modifier.weight(0.75f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                placeholder = { Text(text = "Password") },
                label = { Text(text = "Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Visibility Icon"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val context = LocalContext.current
            Button(
                onClick = {
                    val account = Account(login, accountType)
                    val am = AccountManager.get(context)
                    val accountCreated: Boolean = am.addAccountExplicitly(account, password, null)

                    val activity = context.findActivity()
                    val extras: Bundle? = activity?.intent?.extras
                    if (accountCreated) {  //Pass the new account back to the account manager
                        val result = Bundle()
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, login)
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType)
                        if (extras != null) {
                            extras.getParcelable<AccountAuthenticatorResponse?>(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)
                                ?.onResult(result)
                        }
                    }
                    navigator.navigateUp() // go back
                },
                enabled = true,
            ) {
                Text(text = "Login")
            }
        }
    }
}