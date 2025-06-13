package com.marcello0140.tabungin.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.marcello0140.tabungin.R
import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.ui.screen.formatRupiah
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DialogTambahWishlist(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Launcher Crop Image (Kamera & Galeri)
    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        imageUri = result.uriContent
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tambah_wishlist)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Foto Wishlist
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .clickable {
                            launcher.launch(
                                CropImageContractOptions(
                                    null,
                                    CropImageOptions(
                                        imageSourceIncludeCamera = true,
                                        imageSourceIncludeGallery = true,
                                        cropShape = CropImageView.CropShape.OVAL
                                    )
                                )
                            )
                        }
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Foto Wishlist",
                            modifier = Modifier.size(96.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = "Tambah Foto",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.nama_wishlist)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { if (it.all { c -> c.isDigit() }) targetAmount = it },
                    label = { Text(stringResource(R.string.target_tabungan)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amountInt = targetAmount.toIntOrNull() ?: 0
                if (name.isNotBlank() && amountInt > 0) {
                    onConfirm(name, amountInt, imageUri?.toString())
                }
            }) {
                Text(stringResource(R.string.simpan))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.batal))
            }
        }
    )
}

@Composable
fun DialogEditWishlist(
    initialName: String,
    initialTargetAmount: String,
    initialImageUrl: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String?) -> Unit,
    onPickImage: (() -> Unit)? = null
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var targetAmount by remember(initialTargetAmount) { mutableStateOf(initialTargetAmount) }
    var imageUrl by remember(initialImageUrl) { mutableStateOf(initialImageUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_wishlist)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Foto + tombol edit
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { onPickImage?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    if (!imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Foto Wishlist",
                            modifier = Modifier.size(110.dp)
                        )
                    } else {
                        Text("📦", style = MaterialTheme.typography.displayMedium)
                    }
                    // Icon pensil di pojok kanan atas
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Foto",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // -- Form --
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.nama_wishlist)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { if (it.all { c -> c.isDigit() }) targetAmount = it },
                    label = { Text(stringResource(R.string.target_tabungan)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val targetValue = targetAmount.toIntOrNull() ?: 0
                if (name.isNotBlank() && targetValue > 0) {
                    onConfirm(name, targetValue, imageUrl)
                    onDismiss()
                }
            }) {
                Text(stringResource(R.string.simpan))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.batal))
            }
        }
    )
}


@Composable
fun DialogDeleteWishlist(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hapus_wishlist)) },
        text = { Text(stringResource(R.string.konfirmasi_hapus_wishlist)) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(R.string.ya_hapus), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.batal))
            }
        }
    )
}

@Composable
fun DialogTambahRiwayat(
    onDismiss: () -> Unit,
    onConfirm: (Int, Boolean) -> Unit,
    currentAmount: Int,
    initialNominal: String = "",
    initialIsPenambahan: Boolean = true
) {
    var nominal by remember { mutableStateOf(initialNominal) }
    var isPenambahan by remember { mutableStateOf(initialIsPenambahan) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tambah_edit_catatan)) },
        text = {
            Column {
                OutlinedTextField(
                    value = nominal,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    onValueChange = { if (it.all { c -> c.isDigit() }) nominal = it },
                    label = { Text(stringResource(R.string.nominal)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                SegmentedButton(selected = isPenambahan, onSelectedChange = { isPenambahan = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nominalValue = nominal.toIntOrNull() ?: 0
                when {
                    nominalValue <= 0 -> {
                        Toast.makeText(context, context.getString(R.string.nominal_harus_lebih_dari_nol), Toast.LENGTH_SHORT).show()
                    }
                    !isPenambahan && nominalValue > currentAmount -> {
                        Toast.makeText(context, context.getString(R.string.pengurangan_melebihi_saldo), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        onConfirm(nominalValue, isPenambahan)
                        onDismiss()
                    }
                }
            }) {
                Icon(Icons.Default.Save, contentDescription = stringResource(R.string.simpan))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.simpan))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.batal))
            }
        }
    )
}

@Composable
fun DialogRiwayat(
    historyItem: TabunganHistory,
    onDismiss: () -> Unit,
    onEdit: (historyId: Int) -> Unit,
    onDelete: (historyId: Int) -> Unit
) {
    val isPenambahan = historyItem.isPenambahan
    val nominalColor = if (isPenambahan) Color(0xFF2E7D32) else Color(0xFFC62828)
    val jenisText = if (isPenambahan) stringResource(R.string.penambahan) else stringResource(R.string.pengurangan)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.detail_catatan), fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = jenisText, color = nominalColor, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = formatRupiah(historyItem.nominal), color = nominalColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.tanggal_format, formatDateToReadable(historyItem.tanggal)), fontStyle = FontStyle.Italic)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onEdit(historyItem.id.toInt()) }) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.edit))
                }
                TextButton(onClick = { onDelete(historyItem.id.toInt()) }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.hapus))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.hapus), color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.tutup))
            }
        }
    )
}

fun formatDateToReadable(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yy - HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun PreviewDialogTambahCatatan() {
    var showDialog by remember { mutableStateOf(true) }
    val currentAmount by remember { mutableIntStateOf(100000) }

    if (showDialog) {
        DialogTambahRiwayat(
            onDismiss = { showDialog = false },
            onConfirm = { nominal, isPenambahan ->
                println("Nominal: $nominal, Penambahan: $isPenambahan")
                showDialog = false
            },
            currentAmount = currentAmount
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    PreviewDialogTambahCatatan()
}
