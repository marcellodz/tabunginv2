package com.marcello0140.tabungin.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.marcello0140.tabungin.BuildConfig
import com.marcello0140.tabungin.R
import com.marcello0140.tabungin.model.User
import com.marcello0140.tabungin.model.WishList
import com.marcello0140.tabungin.model.WishListWithHistory
import com.marcello0140.tabungin.navigation.Screen
import com.marcello0140.tabungin.network.UserDataStore
import com.marcello0140.tabungin.ui.components.DialogTambahWishlist
import com.marcello0140.tabungin.ui.components.ProfilDialog
import com.marcello0140.tabungin.ui.viewmodel.MainViewModel
import com.marcello0140.tabungin.ui.viewmodel.ThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    themeViewModel: ThemeViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    // State dialog terpisah!
    var showProfilDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = if (isDarkMode)
                                stringResource(R.string.switch_to_light)
                            else stringResource(R.string.switch_to_dark)
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showProfilDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_wishlist))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.add_wishlist))
                }
            }
        }
    ) { innerPadding ->
        MainScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            wishListWithHistory = viewModel.wishListWithHistory.collectAsState().value
        )

        if (showProfilDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showProfilDialog = false }
            ) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showProfilDialog = false
            }
        }

        if (showAddDialog) {
            DialogTambahWishlist(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, targetAmount, imageUrl ->
                    viewModel.viewModelScope.launch {
                        val newId = viewModel.addWishlist(name, targetAmount, imageUrl)
                        showAddDialog = false
                        navController.navigate(Screen.Detail.navigationWithId(newId.toInt()))
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    wishListWithHistory: List<WishListWithHistory>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.tab_not_reached),
        stringResource(R.string.tab_reached)
    )

    Column(modifier = modifier.padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredList = if (selectedTabIndex == 0) {
            wishListWithHistory.filter {
                val totalAmount = it.histories.sumOf { h ->
                    if (h.isPenambahan) h.nominal else -h.nominal }
                totalAmount < it.wishList.targetAmount
            }
        } else {
            wishListWithHistory.filter {
                val totalAmount = it.histories.sumOf { h ->
                    if (h.isPenambahan) h.nominal else -h.nominal }
                totalAmount >= it.wishList.targetAmount
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SentimentDissatisfied,
                        contentDescription = stringResource(R.string.empty),
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.no_wishlist),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList.size) { index ->
                    val item = filteredList[index]
                    val calculatedAmount = item.histories.sumOf { h ->
                        if (h.isPenambahan) h.nominal else -h.nominal }
                    WishListItem(
                        item = item.wishList.copy(currentAmount = calculatedAmount),
                        onClick = {
                            navController.navigate(Screen.Detail.navigationWithId(item.wishList.id.toInt()))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun WishListItem(item: WishList, onClick: () -> Unit) {
    val progress = item.currentAmount.toFloat() / item.targetAmount
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge)
                Text(stringResource(R.string.currency_amount, item.targetAmount),
                    style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.percentage_collected, percentage, item.currentAmount),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ==== SIGN-IN / SIGN-OUT UTILS ====

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    Log.d("SIGN-IN1", "Mulai proses signIn")
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY) // Client ID!
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        Log.d("SIGN-IN2", "Membuat CredentialManager & request Google login")
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        Log.d("SIGN-IN3", "Berhasil dapat result dari getCredential, memanggil handleSignIn")
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN4", "GetCredentialException: ${e.errorMessage}", e)
    } catch (e: Exception) {
        Log.e("SIGN-IN5", "Exception lain saat signIn: ${e.message}", e)
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    Log.d("SIGN-IN6", "handleSignIn() dipanggil")
    val credential = result.credential
    Log.d("SIGN-IN7", "Result credential: $credential")

    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            Log.d("SIGN-IN8", "Credential cocok GoogleIdTokenCredential, mencoba parse...")
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            Log.d("SIGN-IN8", "Sukses login Google: $nama, $email, $photoUrl")
            dataStore.saveData(User(nama, email, photoUrl))
            Log.d("SIGN-IN9", "User data tersimpan ke DataStore")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN10", "GoogleIdTokenParsingException: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("SIGN-IN11", "Exception saat parsing credential: ${e.message}", e)
        }
    } else {
        Log.e("SIGN-IN12", "Credential bukan GoogleIdTokenCredential, type: ${credential?.javaClass?.simpleName}")
    }
}


private suspend fun signOut(context: Context, dataStore: UserDataStore){
    try{
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful){
        Log.e("IMAGE","Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
        MediaStore.Images.Media.getBitmap(resolver, uri)
    }else{
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

// ==== PREVIEW ====
@Preview(showBackground = true)
@Composable
fun PreviewMainScreenContent() {
    val dummyWishLists = listOf(
        WishListWithHistory(
            wishList = WishList(
                id = 1L,
                name = "Dummy Wish",
                targetAmount = 1000000,
                currentAmount = 500000,
                createdAt = "2025-05-10"
            ),
            histories = listOf()
        )
    )

    MainScreenContent(
        navController = rememberNavController(),
        wishListWithHistory = dummyWishLists
    )
}