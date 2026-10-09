package com.example.zusanfoodie

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    onLoginSuccess: (
        userId: Int,
        name: String,
        role: String
    ) -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val backgroundColor = Color(0xFF3036B5)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 35.dp)
            .padding(top = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        // ================= APP NAME =================

        Text(
            text = "ZUSAN FOODIE",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome Back!",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Login to continue",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(40.dp))


        // ================= EMAIL LABEL =================

        Text(
            text = "EMAIL",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))


        // ================= EMAIL FIELD =================

        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
                message = ""
            },

            placeholder = {
                Text("Enter your email")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(18.dp))


        // ================= PASSWORD LABEL =================

        Text(
            text = "PASSWORD",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))


        // ================= PASSWORD FIELD =================

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
                message = ""
            },

            placeholder = {
                Text("Enter your password")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true,

            shape = RoundedCornerShape(12.dp),

            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            trailingIcon = {

                Image(
                    painter = painterResource(
                        id =
                            if (passwordVisible) {
                                R.drawable.eye_open
                            } else {
                                R.drawable.eye_closed
                            }
                    ),

                    contentDescription =
                        if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        },

                    modifier = Modifier
                        .size(22.dp)
                        .clickable {
                            passwordVisible = !passwordVisible
                        }
                )
            },

            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )


        Spacer(modifier = Modifier.height(35.dp))


        // ================= MESSAGE =================

        if (message.isNotEmpty()) {

            Text(
                text = message,
                color = Color.White,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }


        // ================= LOGIN BUTTON =================

        Button(
            onClick = {

                if (
                    email.isBlank() ||
                    password.isBlank()
                ) {

                    message =
                        "Please enter your email and password"

                } else {

                    isLoading = true
                    message = ""

                    RetrofitClient.apiService
                        .login(
                            email.trim(),
                            password
                        )
                        .enqueue(

                            object : Callback<LoginResponse> {

                                override fun onResponse(
                                    call: Call<LoginResponse>,
                                    response: Response<LoginResponse>
                                ) {

                                    isLoading = false

                                    if (response.isSuccessful) {

                                        val result =
                                            response.body()

                                        if (result?.success == true) {

                                            val userId =
                                                result.user_id

                                            val name =
                                                result.name

                                            val role =
                                                result.role


                                            // Make sure all required
                                            // user information exists

                                            if (
                                                userId != null &&
                                                name != null &&
                                                role != null
                                            ) {

                                                onLoginSuccess(
                                                    userId,
                                                    name,
                                                    role
                                                )

                                            } else {

                                                message =
                                                    "User information not found"
                                            }

                                        } else {

                                            message =
                                                result?.message
                                                    ?: "Invalid email or password"
                                        }

                                    } else {

                                        message =
                                            "Server error"
                                    }
                                }


                                override fun onFailure(
                                    call: Call<LoginResponse>,
                                    t: Throwable
                                ) {

                                    isLoading = false

                                    message =
                                        "Cannot connect to server"
                                }
                            }
                        )
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            shape = RoundedCornerShape(25.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = backgroundColor
            ),

            enabled = !isLoading
        ) {

            Text(
                text =
                    if (isLoading) {
                        "LOGGING IN..."
                    } else {
                        "LOGIN"
                    },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}