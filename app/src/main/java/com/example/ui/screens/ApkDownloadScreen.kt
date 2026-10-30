package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ApkDownloadState

@Composable
fun ApkDownloadScreen(
  state: ApkDownloadState,
  onStartDownload: () -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
  ) {
    // Top Hero Card: Download Latest Android APK
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("apk_download_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.7f))
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.2f))
                .border(1.5.dp, ElectricCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Android,
                contentDescription = "Android Robot",
                tint = ElectricCyan,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Khel11 Fantasy APK",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = EmeraldGreen.copy(alpha = 0.2f),
                  border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldGreen)
                ) {
                  Text(
                    text = "OFFICIAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = EmeraldGreen,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Text(
                text = "Latest Stable Build: ${state.downloadVersion}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = ElectricCyan
              )
              Text(
                text = "Package Size: ${state.apkFileSize} • Android 7.0+",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Download Action Button
          Button(
            onClick = onStartDownload,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("direct_apk_download_btn")
          ) {
            if (state.isDownloading) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.5.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Downloading APK (${(state.progress * 100).toInt()}%)...",
                fontWeight = FontWeight.Black
              )
            } else if (state.isDownloaded) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Downloaded",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("APK DOWNLOAD COMPLETE • TAP TO INSTALL", fontWeight = FontWeight.Black)
            } else {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download APK",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("DOWNLOAD ANDROID APK (v2.4.1)", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
          }

          if (state.isDownloading) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
              progress = { state.progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = EmeraldGreen,
              trackColor = DarkSurface
            )
          }
        }
      }
    }

    // Direct Website Download Link Box
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = "Link",
              tint = GoldAmber,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Direct Website APK Download Link",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Easily accessible public URL to download the APK package directly from any web browser:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Link Display Box
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = state.apkDownloadUrl,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan,
                modifier = Modifier.weight(1f),
                maxLines = 1
              )
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Khel11 APK Link", state.apkDownloadUrl)
                  clipboard.setPrimaryClip(clip)
                  onShowMessage("APK Download link copied to clipboard!")
                },
                modifier = Modifier.size(32.dp).testTag("copy_apk_link_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Link",
                  tint = GoldAmber,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Web App Link Box
          Text(
            text = "Official Web Portal Link:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = state.websiteUrl,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.weight(1f),
                maxLines = 1
              )
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Khel11 Web Link", state.websiteUrl)
                  clipboard.setPrimaryClip(clip)
                  onShowMessage("Website portal URL copied to clipboard!")
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Website Link",
                  tint = GoldAmber,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Share Button
          Button(
            onClick = {
              val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                  Intent.EXTRA_TEXT,
                  "🏏 Download Khel11 Fantasy App and get ₹100 Free Cash Bonus!\n👉 Official APK Download Link: ${state.apkDownloadUrl}\n🌐 Website: ${state.websiteUrl}"
                )
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share APK Link With Friends"))
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
            modifier = Modifier.fillMaxWidth().testTag("share_apk_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("SHARE APK LINK WITH FRIENDS (+₹100 BONUS)", fontWeight = FontWeight.Black)
          }
        }
      }
    }

    // QR Code Scanner Section
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Scan QR to Download on Mobile",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "QR Scanner",
              tint = ElectricCyan
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Render authentic QR Code Graphic
          Box(
            modifier = Modifier
              .size(160.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            QrCodeCanvas(modifier = Modifier.fillMaxSize())
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Point any phone camera or Google Lens at this QR code to download the APK instantly without typing URL",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    // 4-Step Android Installation Guide
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Easy 4-Step Installation Guide",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Spacer(modifier = Modifier.height(12.dp))

          InstallStepItem(
            step = "1",
            title = "Download APK File",
            desc = "Tap 'Download Android APK' button above or visit the website link."
          )
          InstallStepItem(
            step = "2",
            title = "Confirm Download",
            desc = "If prompted with 'File might be harmful', tap 'Download anyway'. (Standard Android safety prompt for apps outside Play Store)."
          )
          InstallStepItem(
            step = "3",
            title = "Allow Install Unknown Apps",
            desc = "Tap the downloaded file. If asked, enable 'Allow from this source' in Settings."
          )
          InstallStepItem(
            step = "4",
            title = "Launch & Win Real Cash",
            desc = "Open Khel11 Fantasy, pick your dream 11 players, and join mega leagues!"
          )
        }
      }
    }

    // Safety & Verification Badges
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurface)
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Verified, contentDescription = "Verified", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Play Protect Safe", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = "Scanned", tint = ElectricCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("No Malware / Root", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Check, contentDescription = "Legal", tint = GoldAmber, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("100% Legal Gaming", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun InstallStepItem(step: String, title: String, desc: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(CrimsonPrimary),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = step,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
        color = Color.White
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Text(
        text = desc,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary
      )
    }
  }
}

@Composable
fun QrCodeCanvas(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val size = size.minDimension
    val moduleCount = 21
    val moduleSize = size / moduleCount

    // Draw finder patterns at 3 corners
    fun drawFinder(topRow: Int, leftCol: Int) {
      // Outer 7x7
      drawRect(
        color = Color.Black,
        topLeft = Offset(leftCol * moduleSize, topRow * moduleSize),
        size = Size(7 * moduleSize, 7 * moduleSize)
      )
      // Inner 5x5 white
      drawRect(
        color = Color.White,
        topLeft = Offset((leftCol + 1) * moduleSize, (topRow + 1) * moduleSize),
        size = Size(5 * moduleSize, 5 * moduleSize)
      )
      // Center 3x3 black
      drawRect(
        color = Color.Black,
        topLeft = Offset((leftCol + 2) * moduleSize, (topRow + 2) * moduleSize),
        size = Size(3 * moduleSize, 3 * moduleSize)
      )
    }

    drawFinder(0, 0)
    drawFinder(0, 14)
    drawFinder(14, 0)

    // Data modules pattern simulation
    val pattern = listOf(
      8 to 8, 8 to 9, 8 to 11, 8 to 12, 9 to 8, 9 to 10, 9 to 12,
      10 to 9, 10 to 11, 11 to 8, 11 to 10, 11 to 12, 12 to 8, 12 to 11,
      2 to 8, 3 to 9, 4 to 10, 5 to 11, 6 to 12, 8 to 2, 9 to 3,
      10 to 4, 11 to 5, 12 to 6, 14 to 8, 15 to 9, 16 to 10, 17 to 11,
      18 to 12, 19 to 8, 20 to 9, 8 to 14, 9 to 15, 10 to 16, 11 to 17,
      12 to 18, 13 to 19, 14 to 14, 15 to 15, 16 to 16, 17 to 17, 18 to 18
    )

    pattern.forEach { (r, c) ->
      drawRect(
        color = Color.Black,
        topLeft = Offset(c * moduleSize, r * moduleSize),
        size = Size(moduleSize, moduleSize)
      )
    }

    // Trophy center emblem
    drawCircle(
      color = Color(0xFFE11D48),
      radius = moduleSize * 2.5f,
      center = Offset(size / 2, size / 2)
    )
  }
}
