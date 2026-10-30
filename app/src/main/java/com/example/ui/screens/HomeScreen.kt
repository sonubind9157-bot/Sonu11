package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Match
import com.example.model.MatchStatus
import com.example.ui.components.MatchFixtureCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  matches: List<Match>,
  onMatchClick: (String) -> Unit,
  onOpenWallet: () -> Unit,
  onOpenApkDownload: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Live, 2: Upcoming

  val filteredMatches = remember(selectedTab, matches) {
    when (selectedTab) {
      1 -> matches.filter { it.status == MatchStatus.LIVE }
      2 -> matches.filter { it.status == MatchStatus.UPCOMING }
      else -> matches
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Hero Banner Card with generated stadium graphics
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .clip(RoundedCornerShape(18.dp))
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.banner_stadium),
          contentDescription = "Fantasy Stadium Championship Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for contrast
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, DarkBg.copy(alpha = 0.95f))
              )
            )
        )

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.Bottom
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CrimsonPrimary)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Mega League",
              tint = Color.White,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "T20 SUPER LEAGUE 2026",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Win Real Cash Prizes Up To ₹5 Crore!",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = (-0.5).sp
            ),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Create your XI, pick C & VC, compete in guaranteed mega contests",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }
    }

    // Quick Action Cards (APK Download Banner & Wallet Offer)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // APK Download Card
        Card(
          onClick = onOpenApkDownload,
          modifier = Modifier
            .weight(1f)
            .testTag("home_apk_download_banner"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Android,
                contentDescription = "Download APK",
                tint = ElectricCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Download APK",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "Get App v2.4",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan
              )
            }
          }
        }

        // Add Cash Offer Card
        Card(
          onClick = onOpenWallet,
          modifier = Modifier
            .weight(1f)
            .testTag("home_wallet_offer_banner"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GoldAmber.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet",
                tint = GoldAmber,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Add Cash",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "+100% Bonus",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAmber
              )
            }
          }
        }
      }
    }

    // Filter Chips: All, Live, Upcoming
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All Matches", "🔥 Live Now", "Upcoming").forEachIndexed { index, label ->
          FilterChip(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            label = {
              Text(
                text = label,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CrimsonPrimary,
              selectedLabelColor = Color.White,
              containerColor = DarkSurface,
              labelColor = TextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selectedTab == index,
              borderColor = DarkSurfaceBorder,
              selectedBorderColor = CrimsonPrimary
            ),
            modifier = Modifier.testTag("match_filter_chip_$index")
          )
        }
      }
    }

    // Match Fixtures Header
    item {
      Text(
        text = "Select Match To Play",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )
    }

    // Fixtures List
    if (filteredMatches.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No matches found in this category",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
          )
        }
      }
    } else {
      items(filteredMatches, key = { it.id }) { match ->
        MatchFixtureCard(
          match = match,
          onClick = { onMatchClick(match.id) },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }
    }

    // Trust & Security Footer
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Why Khel11 Fantasy?",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            SecurityFeatureItem(
              icon = Icons.Default.Shield,
              title = "256-Bit SSL",
              subtitle = "Secure Gateway"
            )
            SecurityFeatureItem(
              icon = Icons.Default.FlashOn,
              title = "Instant IMPS",
              subtitle = "60s Withdrawals"
            )
            SecurityFeatureItem(
              icon = Icons.Default.Gavel,
              title = "100% Legal",
              subtitle = "Skill Gaming"
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SecurityFeatureItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(EmeraldGreen.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = EmeraldGreen,
        modifier = Modifier.size(16.dp)
      )
    }
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextMuted
      )
    }
  }
}
