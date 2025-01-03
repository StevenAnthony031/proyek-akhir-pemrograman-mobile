package com.example.proyekakhirpemrogramanmobile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyekakhirpemrogramanmobile.data.source.Route
import com.example.proyekakhirpemrogramanmobile.ui.screen.HomeScreen
import com.example.proyekakhirpemrogramanmobile.ui.screen.LoginScreen
import com.example.proyekakhirpemrogramanmobile.ui.screen.OnboardingScreen
import com.example.proyekakhirpemrogramanmobile.ui.screen.RegisterScreen
import com.example.proyekakhirpemrogramanmobile.ui.screen.SetupProfileScreen
import com.example.proyekakhirpemrogramanmobile.util.showToast
import com.example.proyekakhirpemrogramanmobile.viewmodel.AuthenticationViewModel
import com.example.proyekakhirpemrogramanmobile.viewmodel.DatabaseViewModel
import com.example.proyekakhirpemrogramanmobile.viewmodel.LoadingViewModel

@Composable
fun MainApp(context: Context) {
    val authenticationViewModel: AuthenticationViewModel = viewModel()
    val userAuthState by authenticationViewModel.userAuthState.collectAsState()
    val errorEmailState by authenticationViewModel.errorEmailState.collectAsState()
    val errorPasswordState by authenticationViewModel.errorPasswordState.collectAsState()
    val errorFullNameState by authenticationViewModel.errorFullNameState.collectAsState()
    val errorStudentIdState by authenticationViewModel.errorStudentIdState.collectAsState()
    val errorGenderState by authenticationViewModel.errorGenderState.collectAsState()
    val errorAllState by authenticationViewModel.errorAllState.collectAsState()

    val databaseViewModel: DatabaseViewModel = viewModel()
    val userDataState by databaseViewModel.userDataState.collectAsState()

    val loadingViewModel: LoadingViewModel = viewModel()
    val loadingState by loadingViewModel.loadingState.collectAsState()

    val navController: NavHostController = rememberNavController()
    val navigateTo: (String, Boolean) -> Unit = { route, clearStack ->
        navController.navigate(route) {
            if (clearStack) {
                popUpTo(0) {
                    inclusive = true
                }
            } else {
                popUpTo(route) {
                    inclusive = true
                }
            }
        }
    }

    val startDestination = Route.ONBOARDING_SCREEN.name

    LaunchedEffect(Unit) {
        databaseViewModel.cloudinaryInitialization(context)

    }

    NavHost(
        navController = navController,
        startDestination =  startDestination
    ) {
        // Route Onboarding Screen
        composable(Route.ONBOARDING_SCREEN.name) {
            OnboardingScreen(
                onStartButtonClicked = {
                    navigateTo(Route.LOGIN_SCREEN.name, false)
                }
            )
        }

        // Route Login Screen
        composable(Route.LOGIN_SCREEN.name) {
            LoginScreen(
                errorEmailState = errorEmailState,
                errorPasswordState = errorPasswordState,
                errorAllState = errorAllState,
                loadingState = loadingState,
                onLoginButtonClicked = { email, password ->
                    authenticationViewModel.login(
                        email = email,
                        password = password,
                        showLoading = { state ->
                            loadingViewModel.showLoading(state)
                        },
                        onSuccess = { userId ->
                            databaseViewModel.checkUserFromDatabase(
                                userId = userId,
                                onUserExist = {
                                    databaseViewModel.getUserFromDatabase(
                                        userId = userId,
                                        showLoading = { state ->
                                            loadingViewModel.showLoading(state)
                                        },
                                        onSuccess = {
                                            navigateTo(Route.HOME_SCREEN.name, true)
                                        },
                                        onFailure = {
                                            showToast(context, "Proses masuk gagal, coba kembali")
                                        }
                                    )
                                },
                                onUserNotExist = {
                                    authenticationViewModel.clearErrorState()
                                    navigateTo(Route.SETUP_PROFILE_SCREEN.name, false)
                                },
                                onFailure = {
                                    showToast(context, "Proses masuk gagal, coba kembali")
                                }
                            )
                        },
                        onFailure = {
                            showToast(context, "Proses masuk gagal, coba kembali")
                        }
                    )
                },
                onRegisterButtonClicked = {
                    authenticationViewModel.clearErrorState()
                    navigateTo(Route.REGISTER_SCREEN.name, false)
                }
            )
        }

        // Route Register Screen
        composable(Route.REGISTER_SCREEN.name) {
            RegisterScreen(
                errorEmailState = errorEmailState,
                errorPasswordState = errorPasswordState,
                loadingState = loadingState,
                onRegisterButtonClicked = { email, password ->
                    authenticationViewModel.register(
                        email = email,
                        password = password,
                        showLoading = { state ->
                            loadingViewModel.showLoading(state)
                        },
                        onSuccess = {
                            authenticationViewModel.clearErrorState()
                            navigateTo(Route.SETUP_PROFILE_SCREEN.name, false)
                        },
                        onFailure = {
                            showToast(context, "Pendaftaran gagal, coba kembali")
                        }
                    )
                },
                onLoginButtonClicked = {
                    authenticationViewModel.clearErrorState()
                    navigateTo(Route.LOGIN_SCREEN.name, false)
                }
            )
        }

        // Route Setup Profile Screen
        composable(Route.SETUP_PROFILE_SCREEN.name) {
            SetupProfileScreen(
                errorFullNameState = errorFullNameState,
                errorStudentIdState = errorStudentIdState,
                errorGenderState = errorGenderState,
                loadingState = loadingState,
                onFinishButtonClicked = { fullName, studentId, gender ->
                    val fullNameValid = authenticationViewModel.isFullNameInputValid(fullName)
                    val studentIdValid = authenticationViewModel.isStudentIdValid(studentId)
                    val genderValid = authenticationViewModel.isGenderValid(gender)
                    if (fullNameValid && studentIdValid && genderValid) {
                        databaseViewModel.addUserToDatabase(
                            userId = userAuthState!!.uid,
                            email = userAuthState!!.email!!,
                            fullName = fullName,
                            studentId = studentId,
                            gender = gender,
                            showLoading = { state ->
                                loadingViewModel.showLoading(state)
                            },
                            onSuccess = {
                                navigateTo(Route.HOME_SCREEN.name, true)
                            },
                            onFailure = {
                                showToast(context, "Pendaftaran gagal, coba kembali")
                            }
                        )
                    }
                }
            )
        }

        // Route Home Screen
        composable(Route.HOME_SCREEN.name) {
            HomeScreen()
        }

        // ===========================
        //      FINAL SECTION END
        // ===========================

//        coroutineScope.launch {
//            if (userAuthState != null) {
//                if (databaseViewModel.userExistInDatabase(userAuthState!!.uid)) {
//                    navController.navigate("home_screen") {
//                        popUpTo(0) {
//                            inclusive = true
//                        }
//                    }
//                } else {
//                    navController.navigate("setup_profile_screen") {
//                        popUpTo(0) {
//                            inclusive = true
//                        }
//                    }
//                }
//            }
//        }
//        composable("base_screen") {
//            BaseScreen(
//                onRegisterScreenButton = {
//                    navController.navigate("register_screen") {
//                        popUpTo("register_screen") {
//                            inclusive = true
//                        }
//                    }
//                },
//                onLoginScreenButton = {
//                    navController.navigate("login_screen") {
//                        popUpTo("login_screen") {
//                            inclusive = true
//                        }
//                    }
//                }
//            )
//        }
//        composable("register_screen") {
//            RegisterScreen(
//                onRegisterButtonClicked = { email, password ->
//                    if (email.isEmpty() or password.isEmpty()) {
//                        showToast(context, "Email or password cannot be empty")
//                    } else {
//                        coroutineScope.launch {
//                            val result = authenticationViewModel.register(email, password)
//                            if (result == "Successful") {
//                                navController.navigate("setup_profile_screen") {
//                                    popUpTo(0) {
//                                        inclusive = true
//                                    }
//                                }
//                            }
//                            showToast(context, result)
//                        }
//                    }
//                },
//                onLoginScreenButtonClicked = {
//                    navController.navigate("login_screen") {
//                        popUpTo("login_screen") {
//                            inclusive = true
//                        }
//                    }
//                },
//                onBaseScreenButtonClicked = {
//                    navController.navigate("base_screen") {
//                        popUpTo("base_screen") {
//                            inclusive = true
//                        }
//                    }
//                }
//            )
//        }
//        composable("login_screen") {
//            LoginScreen(
//                onLoginButtonClicked = { email, password ->
//                    if (email.isEmpty() or password.isEmpty()) {
//                        showToast(context, "Email or password cannot be empty")
//                    } else {
//                        coroutineScope.launch {
//                            val result = authenticationViewModel.login(email, password)
//                            if (result == "Successful") {
//                                navController.navigate("home_screen") {
//                                    popUpTo(0) {
//                                        inclusive = true
//                                    }
//                                }
//                            }
//                            showToast(context, result)
//                        }
//                    }
//                },
//                onRegisterScreenButtonClicked = {
//                    navController.navigate("register_screen") {
//                        popUpTo("register_screen") {
//                            inclusive = true
//                        }
//                    }
//                },
//                onBaseScreenButtonClicked = {
//                    navController.navigate("base_screen") {
//                        popUpTo("base_screen") {
//                            inclusive = true
//                        }
//                    }
//                }
//            )
//        }
//        composable("setup_profile_screen") {
//            SetupProfileScreen(
//                onSetupProfileButtonClicked = { fullName, studentId -> // todo
//                    coroutineScope.launch {
//                        val result = databaseViewModel.addUserToDatabase(userAuthState!!, fullName, studentId)
//                        if (result == "Successful") {
//                            navController.navigate("home_screen") {
//                                popUpTo(0) {
//                                    inclusive = true
//                                }
//                            }
//                        }
//                        showToast(context, result)
//                    }
//                }
//            )
//        }
//        composable("home_screen") {
//            HomeScreen(
//                imageUrl = databaseViewModel.getImageUrlFromCloudinary(), // todo
//                email = userAuthState?.email,
//                onLogoutButtonClicked = {
//                    authenticationViewModel.logout()
//                    navController.navigate("base_screen") {
//                        popUpTo(0) {
//                            inclusive = true
//                        }
//                    }
//                    showToast(context, "Successful")
//                }
//            )
//        }
    }

}