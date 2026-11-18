package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.cloud.CloudSyncRepository
import com.example.data.local.AppDatabase
import com.example.data.local.DebtLedgerEntity
import com.example.data.preference.AppPreferences
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class DebtFilter {
    ALL,
    RECEIVABLE, // পাওনা (ঋণ দিয়েছি)
    PAYABLE,    // দেনা (ঋণ নিয়েছি)
    SETTLED     // পরিশোধিত
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtLedgerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }
    val dao = remember { database.debtLedgerDao() }
    val prefs = remember { AppPreferences(context) }
    val cloudSync = remember { CloudSyncRepository(context, prefs, database) }
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val currentUserId = firebaseAuth.currentUser?.uid ?: ""

    val allDebts by dao.getAllEntries().collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf(DebtFilter.ALL) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<DebtLedgerEntity?>(null) }
    var previewImageUri by remember { mutableStateOf<String?>(null) }
    var entryToDelete by remember { mutableStateOf<DebtLedgerEntity?>(null) }

    // Summary calculations
    val totalReceivable = remember(allDebts) {
        allDebts.filter { it.type == "GIVE" && !it.isSettled }.sumOf { it.amount }
    }
    val totalPayable = remember(allDebts) {
        allDebts.filter { it.type == "TAKE" && !it.isSettled }.sumOf { it.amount }
    }
    val netBalance = remember(totalReceivable, totalPayable) {
        totalReceivable - totalPayable
    }

    val filteredList = remember(allDebts, selectedFilter) {
        when (selectedFilter) {
            DebtFilter.ALL -> allDebts
            DebtFilter.RECEIVABLE -> allDebts.filter { it.type == "GIVE" && !it.isSettled }
            DebtFilter.PAYABLE -> allDebts.filter { it.type == "TAKE" && !it.isSettled }
            DebtFilter.SETTLED -> allDebts.filter { it.isSettled }
        }
    }

    val currencyFormatter = remember(uiLang) {
        val format = NumberFormat.getNumberInstance(
            com.example.data.util.SupportedLanguage.fromCode(uiLang).locale
        )
        format.maximumFractionDigits = 0
        format
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiText("ঋণ ও লেনদেন খাতা", "Debt & Loan Ledger"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = uiText(
                                "স্মার্ট দেনা-পাওনা ও প্রমাণপত্র ব্যাকআপ",
                                "Smart receivable/payable tracking & proof backup"
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("debt_ledger_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = uiText("ফিরে যান", "Back"),
                            tint = ChampagneGold
                        )
                    }
                },
                actions = {
                    // Manual sync button
                    if (currentUserId.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    cloudSync.backupAllLocalData(currentUserId)
                                    Toast.makeText(
                                        context,
                                        localizedText(uiLang, "ক্লাউডে সংরক্ষিত ও সিঙ্ক হয়েছে", "Saved & synced to cloud"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDone,
                                contentDescription = uiText("ক্লাউড সিঙ্ক", "Cloud Sync"),
                                tint = MintGlow
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingEntry = null
                    showAddDialog = true
                },
                containerColor = ChampagneGold,
                contentColor = Color.Black,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(uiText("নতুন হিসাব", "New Entry"), fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_debt_entry_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Financial Summary Card
            FinancialSummaryCard(
                totalReceivable = totalReceivable,
                totalPayable = totalPayable,
                netBalance = netBalance,
                currencyFormatter = currencyFormatter
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DebtFilterChip(
                    label = uiText("সকল (${formatLocalizedInt(allDebts.size, uiLang)})", "All (${allDebts.size})"),
                    isSelected = selectedFilter == DebtFilter.ALL,
                    onClick = { selectedFilter = DebtFilter.ALL },
                    modifier = Modifier.weight(1f)
                )
                DebtFilterChip(
                    label = uiText("পাওনা", "Receivable"),
                    isSelected = selectedFilter == DebtFilter.RECEIVABLE,
                    color = MintGlow,
                    onClick = { selectedFilter = DebtFilter.RECEIVABLE },
                    modifier = Modifier.weight(1f)
                )
                DebtFilterChip(
                    label = uiText("দেনা", "Payable"),
                    isSelected = selectedFilter == DebtFilter.PAYABLE,
                    color = Color(0xFFFF5252),
                    onClick = { selectedFilter = DebtFilter.PAYABLE },
                    modifier = Modifier.weight(1f)
                )
                DebtFilterChip(
                    label = uiText("পরিশোধিত", "Settled"),
                    isSelected = selectedFilter == DebtFilter.SETTLED,
                    color = ChampagneGold,
                    onClick = { selectedFilter = DebtFilter.SETTLED },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Debt Records List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = SlateGrayMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (selectedFilter) {
                                DebtFilter.ALL -> uiText("কোনো দেনা-পাওনার হিসাব নেই", "No debt or loan entries yet")
                                DebtFilter.RECEIVABLE -> uiText("কোনো পাওনা হিসাব নেই", "No receivable entries")
                                DebtFilter.PAYABLE -> uiText("কোনো দেনা হিসাব নেই", "No payable entries")
                                DebtFilter.SETTLED -> uiText("কোনো পরিশোধিত হিসাব নেই", "No settled entries")
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiText(
                                "নিচের '+' বাটনে চাপ দিয়ে চুক্তিপত্র ও হিসাব যুক্ত করুন",
                                "Tap the '+' button below to add an entry and agreement proof"
                            ),
                            color = SlateGrayMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList, key = { it.id }) { entry ->
                        DebtEntryCard(
                            entry = entry,
                            currencyFormatter = currencyFormatter,
                            onToggleSettled = {
                                val newSettled = !entry.isSettled
                                val settledTime = if (newSettled) System.currentTimeMillis() else null
                                coroutineScope.launch {
                                    dao.updateSettlementStatus(entry.id, newSettled, settledTime)
                                    val updated = entry.copy(
                                        isSettled = newSettled,
                                        settledDate = settledTime,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    cloudSync.autoSyncDebtEntry(updated)
                                    val msg = if (newSettled) {
                                        localizedText(uiLang, "হিসাবটি পরিশোধিত হিসেবে চিহ্নিত করা হয়েছে", "Entry marked as settled")
                                    } else {
                                        localizedText(uiLang, "হিসাবটি পুনরায় সক্রিয় করা হয়েছে", "Entry reopened as active")
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEdit = {
                                editingEntry = entry
                                showAddDialog = true
                            },
                            onDelete = {
                                entryToDelete = entry
                            },
                            onPreviewProof = { uri ->
                                previewImageUri = uri
                            },
                            onShareReceipt = {
                                val dateLocale = com.example.data.util.SupportedLanguage.fromCode(uiLang).locale
                                val receiptText = buildString {
                                    if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn") {
                                        val typeText = if (entry.type == "GIVE") "Loan Given (Receivable)" else "Loan Taken (Payable)"
                                        appendLine("📜 Noor-e-Hidayah Debt Agreement / Receipt")
                                        appendLine("--------------------------------")
                                        appendLine("Type: $typeText")
                                        appendLine("Person: ${entry.personName}")
                                        if (entry.phoneNumber.isNotEmpty()) {
                                            appendLine("Phone: ${entry.phoneNumber}")
                                        }
                                        appendLine("Amount: ৳ ${currencyFormatter.format(entry.amount)} BDT")
                                        val dateStr = SimpleDateFormat("dd MMMM, yyyy", dateLocale).format(Date(entry.transactionDate))
                                        appendLine("Date: $dateStr")
                                        entry.dueDate?.let { due ->
                                            val dueStr = SimpleDateFormat("dd MMMM, yyyy", dateLocale).format(Date(due))
                                            appendLine("Due Date: $dueStr")
                                        }
                                        if (entry.notes.isNotEmpty()) {
                                            appendLine("Notes: ${entry.notes}")
                                        }
                                        if (entry.proofAttachmentNote != null) {
                                            appendLine("Proof/Witness: ${entry.proofAttachmentNote}")
                                        }
                                        val status = if (entry.isSettled) "Settled" else "Unpaid"
                                        appendLine("Status: $status")
                                        appendLine("--------------------------------")
                                        appendLine("Recorded truthfully with Allah as our Witness.")
                                    } else {
                                        val typeText = if (entry.type == "GIVE") "ঋণ প্রদান (পাওনা)" else "ঋণ গ্রহণ (দেনা)"
                                        appendLine("📜 নূর-এ-হেদায়েত ঋণ ও লেনদেন চুক্তিপত্র/রসিদ")
                                        appendLine("--------------------------------")
                                        appendLine("ধরণ: $typeText")
                                        appendLine("ব্যক্তির নাম: ${entry.personName}")
                                        if (entry.phoneNumber.isNotEmpty()) {
                                            appendLine("মোবাইল নম্বর: ${entry.phoneNumber}")
                                        }
                                        appendLine("টাকার পরিমাণ: ৳ ${currencyFormatter.format(entry.amount)} টাকা")
                                        val dateStr = SimpleDateFormat("dd MMMM, yyyy", dateLocale).format(Date(entry.transactionDate))
                                        appendLine("লেনদেনের তারিখ: $dateStr")
                                        entry.dueDate?.let { due ->
                                            val dueStr = SimpleDateFormat("dd MMMM, yyyy", dateLocale).format(Date(due))
                                            appendLine("পরিশোধের মেয়াদ: $dueStr")
                                        }
                                        if (entry.notes.isNotEmpty()) {
                                            appendLine("বিবরণ: ${entry.notes}")
                                        }
                                        if (entry.proofAttachmentNote != null) {
                                            appendLine("চুক্তি/সাক্ষী: ${entry.proofAttachmentNote}")
                                        }
                                        val status = if (entry.isSettled) "পরিশোধিত (Settled)" else "বকেয়া (Unpaid)"
                                        appendLine("বর্তমান অবস্থা: $status")
                                        appendLine("--------------------------------")
                                        appendLine("আল্লাহকে সাক্ষী রেখে সত্যতার সাথে হিসাব সংরক্ষণ করা হয়েছে।")
                                    }
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Debt Receipt", receiptText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    localizedText(uiLang, "লেনদেন রসিদ ক্লিপবোর্ডে কপি করা হয়েছে", "Receipt copied to clipboard"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onCallPerson = {
                                if (entry.phoneNumber.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${entry.phoneNumber}")
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Entry Dialog
    if (showAddDialog) {
        DebtEntryDialog(
            initialEntry = editingEntry,
            onDismiss = { showAddDialog = false },
            onSave = { entry ->
                coroutineScope.launch {
                    dao.insertOrUpdate(entry)
                    cloudSync.autoSyncDebtEntry(entry)
                    showAddDialog = false
                    Toast.makeText(
                        context,
                        localizedText(uiLang, "হিসাব সংরক্ষিত ও ক্লাউডে সিঙ্ক হয়েছে", "Entry saved & synced to cloud"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // Full Screen Image Preview Modal
    previewImageUri?.let { uri ->
        Dialog(onDismissRequest = { previewImageUri = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkEmeraldSurface)
                    .border(1.dp, ChampagneGold, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiText("চুক্তিপত্র বা প্রমাণপত্র", "Agreement or Proof"),
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { previewImageUri = null }) {
                            Icon(Icons.Filled.Close, contentDescription = uiText("বন্ধ করুন", "Close"), tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AsyncImage(
                        model = uri,
                        contentDescription = uiText("প্রমাণপত্রের ছবি", "Proof Attachment"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            containerColor = DarkEmeraldSurface,
            title = {
                Text(
                    text = uiText("হিসাবটি মুছে ফেলবেন?", "Delete this entry?"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = uiText(
                        "${entry.personName}-এর সাথে ৳ ${currencyFormatter.format(entry.amount)} টাকার হিসাবটি স্থায়ীভাবে মুছে যাবে।",
                        "The ৳ ${currencyFormatter.format(entry.amount)} entry with ${entry.personName} will be permanently deleted."
                    ),
                    color = SlateGrayMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            dao.deleteById(entry.id)
                            cloudSync.autoRemoveDebtEntry(entry.id)
                            entryToDelete = null
                            Toast.makeText(
                                context,
                                localizedText(uiLang, "হিসাব মুছে ফেলা হয়েছে", "Entry deleted"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text(uiText("মুছে ফেলুন", "Delete"), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                }
            }
        )
    }
}

@Composable
fun FinancialSummaryCard(
    totalReceivable: Double,
    totalPayable: Double,
    netBalance: Double,
    currencyFormatter: NumberFormat
) {
    FrostedGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = ChampagneGold.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("আর্থিক সারসংক্ষেপ", "Financial Summary"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = uiText("মুদ্রা: BDT (৳)", "Currency: BDT (৳)"),
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Total Receivable (পাওনা)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("মোট পাওনা (দিয়েছি)", "Total Receivable (Lent)"),
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳ ${currencyFormatter.format(totalReceivable)}",
                        color = MintGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(HairlineBorder)
                )

                // Total Payable (দেনা)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = uiText("মোট দেনা (নিয়েছি)", "Total Payable (Borrowed)"),
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "৳ ${currencyFormatter.format(totalPayable)}",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = HairlineBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Net Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("নেট উদ্বৃত্ত / ভারসাম্য:", "Net Balance:"),
                    color = SlateGrayMuted,
                    fontSize = 12.sp
                )
                val netColor = if (netBalance >= 0) MintGlow else Color(0xFFFF5252)
                val prefix = if (netBalance >= 0) "+" else ""
                Text(
                    text = "$prefix৳ ${currencyFormatter.format(netBalance)}",
                    color = netColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun DebtFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ChampagneGold
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.2f) else DarkEmeraldSurface)
            .border(
                width = 0.5.dp,
                color = if (isSelected) color else HairlineBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) color else SlateGrayMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun DebtEntryCard(
    entry: DebtLedgerEntity,
    currencyFormatter: NumberFormat,
    onToggleSettled: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPreviewProof: (String) -> Unit,
    onShareReceipt: () -> Unit,
    onCallPerson: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiLang = LocalAppLanguage.current
    val isGive = entry.type == "GIVE"
    val typeColor = if (isGive) MintGlow else Color(0xFFFF5252)
    val typeLabel = if (isGive) {
        uiText("পাওনা (ঋণ দিয়েছি)", "Receivable (Lent)")
    } else {
        uiText("দেনা (ঋণ নিয়েছি)", "Payable (Borrowed)")
    }
    val dateLocale = com.example.data.util.SupportedLanguage.fromCode(uiLang).locale
    val dateStr = remember(entry.transactionDate, uiLang) {
        SimpleDateFormat("dd MMM, yyyy", dateLocale).format(Date(entry.transactionDate))
    }
    val dueStr = remember(entry.dueDate, uiLang) {
        entry.dueDate?.let {
            SimpleDateFormat("dd MMM, yyyy", dateLocale).format(Date(it))
        }
    }

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("debt_card_${entry.id}"),
        cornerRadius = 16.dp,
        backgroundColor = if (entry.isSettled) DarkEmeraldSurface.copy(alpha = 0.6f) else DarkEmeraldSurface,
        borderColor = if (entry.isSettled) ChampagneGold.copy(alpha = 0.3f) else typeColor.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Header Row: Type Badge, Settled Status, Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(typeColor.copy(alpha = 0.15f))
                            .border(0.5.dp, typeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            color = typeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (entry.isSettled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChampagneGold.copy(alpha = 0.2f))
                                .border(0.5.dp, ChampagneGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiText("পরিশোধিত ✓", "Settled ✓"),
                                color = ChampagneGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Amount
                Text(
                    text = "৳ ${currencyFormatter.format(entry.amount)}",
                    color = if (entry.isSettled) SlateGrayMuted else typeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Person Name & Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = entry.personName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (entry.phoneNumber.isNotEmpty()) {
                            Text(
                                text = entry.phoneNumber,
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Call Button (if phone present)
                if (entry.phoneNumber.isNotEmpty()) {
                    IconButton(
                        onClick = onCallPerson,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Call",
                            tint = MintGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dates Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiText("তারিখ: $dateStr", "Date: $dateStr"),
                    color = SlateGrayMuted,
                    fontSize = 11.sp
                )
                if (dueStr != null) {
                    Text(
                        text = uiText("মেয়াদ: $dueStr", "Due: $dueStr"),
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Notes / Terms
            if (entry.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.notes,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // Attached Proof Attachment Preview (if any)
            if (!entry.proofAttachmentUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onPreviewProof(entry.proofAttachmentUri) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = uiText("চুক্তিপত্র বা রসিদ সংযুক্ত", "Agreement or receipt attached"),
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            entry.proofAttachmentNote?.let { note ->
                                val displayNote = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) != "bn" && note == "চুক্তিপত্রের ছবি সংরক্ষিত") {
                                    "Agreement image attached"
                                } else {
                                    note
                                }
                                Text(
                                    text = displayNote,
                                    color = SlateGrayMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = uiText("দেখুন 🔍", "View 🔍"),
                        color = MintGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = HairlineBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(6.dp))

            // Action Toolbar (Mark Settled, Share Receipt, Edit, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle Settled Button
                OutlinedButton(
                    onClick = onToggleSettled,
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (entry.isSettled) ChampagneGold else MintGlow
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (entry.isSettled) ChampagneGold.copy(alpha = 0.5f) else MintGlow.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (entry.isSettled) Icons.Filled.RestartAlt else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (entry.isSettled) {
                            uiText("পুনরায় সক্রিয়", "Reopen")
                        } else {
                            uiText("পরিশোধিত করুন", "Mark Settled")
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Share Receipt
                    IconButton(
                        onClick = onShareReceipt,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = SlateGrayMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Edit
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = SlateGrayMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DebtEntryDialog(
    initialEntry: DebtLedgerEntity?,
    onDismiss: () -> Unit,
    onSave: (DebtLedgerEntity) -> Unit
) {
    val uiLang = LocalAppLanguage.current
    var type by remember { mutableStateOf(initialEntry?.type ?: "GIVE") }
    var personName by remember { mutableStateOf(initialEntry?.personName ?: "") }
    var phoneNumber by remember { mutableStateOf(initialEntry?.phoneNumber ?: "") }
    var amountText by remember { mutableStateOf(initialEntry?.amount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var notes by remember { mutableStateOf(initialEntry?.notes ?: "") }
    var proofUri by remember { mutableStateOf(initialEntry?.proofAttachmentUri) }
    var proofNote by remember { mutableStateOf(initialEntry?.proofAttachmentNote ?: "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            proofUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Text(
                text = if (initialEntry == null) {
                    uiText("নতুন দেনা-পাওনা হিসাব", "New Debt / Loan Entry")
                } else {
                    uiText("হিসাব সম্পাদনা", "Edit Entry")
                },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                // Type Selector: GIVE (পাওনা) vs TAKE (দেনা)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "GIVE") MintGlow.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
                            .border(
                                1.dp,
                                if (type == "GIVE") MintGlow else HairlineBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { type = "GIVE" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiText("ঋণ দিয়েছি (পাওনা)", "Lent (Receivable)"),
                            color = if (type == "GIVE") MintGlow else SlateGrayMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "TAKE") Color(0xFFFF5252).copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
                            .border(
                                1.dp,
                                if (type == "TAKE") Color(0xFFFF5252) else HairlineBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { type = "TAKE" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiText("ঋণ নিয়েছি (দেনা)", "Borrowed (Payable)"),
                            color = if (type == "TAKE") Color(0xFFFF5252) else SlateGrayMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Person Name
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text(uiText("ব্যক্তির নাম *", "Person Name *"), color = SlateGrayMuted, fontSize = 12.sp) },
                    placeholder = { Text(uiText("যেমন: আব্দুল্লাহ ভাই", "e.g. Abdullah"), color = SlateGrayMuted, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Amount in BDT
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(uiText("টাকার পরিমাণ (BDT ৳) *", "Amount (BDT ৳) *"), color = SlateGrayMuted, fontSize = 12.sp) },
                    placeholder = { Text(uiText("যেমন: 5000", "e.g. 5000"), color = SlateGrayMuted, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone Number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(uiText("মোবাইল নম্বর (ঐচ্ছিক)", "Phone Number (Optional)"), color = SlateGrayMuted, fontSize = 12.sp) },
                    placeholder = { Text("017XXXXXXXX", color = SlateGrayMuted, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Notes / Description
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(uiText("বিবরণ বা শর্তাবলী (ঐচ্ছিক)", "Notes or Terms (Optional)"), color = SlateGrayMuted, fontSize = 12.sp) },
                    placeholder = { Text(uiText("উদ্দেশ্য বা লেনদেনের বিবরণ...", "Purpose or transaction details..."), color = SlateGrayMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Photo/Media Attachment Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText("চুক্তিপত্র বা প্রমাণ সংযুক্ত করুন:", "Attach agreement or proof:"),
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold),
                        border = BorderStroke(0.5.dp, ChampagneGold.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (proofUri == null) {
                                uiText("ছবি তুলুন/বাছুন", "Pick Photo")
                            } else {
                                uiText("পরিবর্তন", "Change")
                            },
                            fontSize = 11.sp
                        )
                    }
                }

                if (proofUri != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiText("✓ ১টি প্রমাণপত্র সংযুক্ত আছে", "✓ 1 proof image attached"),
                            color = MintGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = { proofUri = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = uiText("অপসারণ", "Remove"), tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull()
                    if (personName.isBlank() || amountVal == null || amountVal <= 0) {
                        return@Button
                    }
                    val cleanPerson = com.example.data.security.InputSanitizer.sanitizeTextInput(personName, 100)
                    val cleanPhone = com.example.data.security.InputSanitizer.sanitizePhoneNumber(phoneNumber)
                    val cleanNotes = com.example.data.security.InputSanitizer.sanitizeTextInput(notes, 500)
                    val safeAmount = com.example.data.security.InputSanitizer.sanitizeAmount(amountVal)
                    val safeProofUri = if (com.example.data.security.InputSanitizer.isSafeAttachmentUri(proofUri)) proofUri else null

                    val entry = DebtLedgerEntity(
                        id = initialEntry?.id ?: UUID.randomUUID().toString(),
                        type = type,
                        personName = cleanPerson,
                        phoneNumber = cleanPhone,
                        amount = safeAmount,
                        transactionDate = initialEntry?.transactionDate ?: System.currentTimeMillis(),
                        dueDate = initialEntry?.dueDate,
                        notes = cleanNotes,
                        proofAttachmentUri = safeProofUri,
                        proofAttachmentType = if (safeProofUri != null) "IMAGE" else null,
                        proofAttachmentNote = if (safeProofUri != null) {
                            localizedText(uiLang, "চুক্তিপত্রের ছবি সংরক্ষিত", "Agreement image attached")
                        } else null,
                        isSettled = initialEntry?.isSettled ?: false,
                        settledDate = initialEntry?.settledDate,
                        createdAt = initialEntry?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(entry)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
            ) {
                Text(uiText("সংরক্ষণ করুন", "Save"), color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
            }
        }
    )
}
