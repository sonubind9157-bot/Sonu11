package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FantasyTopBar(
  title: String = "Khel11 Fantasy",
  walletBalance: Double,
  showBackButton: Boolean = false,
  onBackClick: () -> Unit = {},
  onWalletClick: () -> Unit = {},
  onApkClick: () -> Unit = {}
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(CrimsonPrimary, GoldAmber)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Khel11 Trophy Logo",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Official Fantasy League",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = GoldAmber
          )
        }
      }
    },
    navigationIcon = {
      if (showBackButton) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("top_bar_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
      }
    },
    actions = {
      // APK Shortcut Button
      IconButton(
        onClick = onApkClick,
        modifier = Modifier.testTag("top_bar_apk_button")
      ) {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = "Download APK",
          tint = ElectricCyan
        )
      }

      // Quick Wallet Balance Pill
      Surface(
        onClick = onWalletClick,
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.5f)),
        modifier = Modifier
          .padding(end = 12.dp)
          .testTag("top_bar_wallet_chip")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = "Wallet Balance",
            tint = GoldAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "₹${walletBalance.toInt()}",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Add Funds",
            tint = EmeraldGreen,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = DarkBg,
      titleContentColor = Color.White
    )
  )
}

@Composable
fun MatchFixtureCard(
  match: Match,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .testTag("match_card_${match.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (match.status == MatchStatus.LIVE) CrimsonPrimary.copy(alpha = 0.8f) else DarkSurfaceBorder
    )
  ) {
    Column {
      // Header: Tournament Name & Countdown / Live Indicator
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurfaceElevated)
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = match.seriesName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextSecondary
          )
          Text(
            text = " • ${match.matchNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
          )
        }

        if (match.status == MatchStatus.LIVE) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(CrimsonPrimary)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.White)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "LIVE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = "Time Left",
              tint = GoldAmber,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = match.timeRemainingText,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = GoldAmber
            )
          }
        }
      }

      // Teams vs Layout
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Team 1
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = match.team1FlagEmoji,
            fontSize = 24.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = match.team1Code,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = Color.White
            )
            Text(
              text = match.team1Name,
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary
            )
            if (match.liveTeam1Score != null) {
              Text(
                text = match.liveTeam1Score,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
              )
            }
          }
        }

        // Center VS Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "VS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = TextMuted
          )
        }

        // Team 2
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = match.team2Code,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = Color.White
            )
            Text(
              text = match.team2Name,
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary
            )
            if (match.liveTeam2Score != null) {
              Text(
                text = match.liveTeam2Score,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = match.team2FlagEmoji,
            fontSize = 24.sp
          )
        }
      }

      // Footer: Mega Prize Pool and Stadium Venue
      HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface.copy(alpha = 0.8f))
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = "Mega Prize",
            tint = GoldAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Mega Prize: ${match.megaPrizePool}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }

        Text(
          text = match.venue,
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun ContestItemCard(
  contest: Contest,
  onJoinClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("contest_card_${contest.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Prize Pool & Entry Fee
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Prize Pool",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Text(
            text = if (contest.prizePool > 0) "₹${formatCurrency(contest.prizePool)}" else "Practice",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = Color.White
          )
        }

        // Entry Button
        Button(
          onClick = onJoinClick,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (contest.isJoined) EmeraldDark else CrimsonPrimary
          ),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          modifier = Modifier.testTag("join_contest_btn_${contest.id}")
        ) {
          if (contest.isJoined) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Joined",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "JOINED",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          } else {
            Text(
              text = if (contest.entryFee > 0) "₹${contest.entryFee.toInt()}" else "FREE",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress Bar
      LinearProgressIndicator(
        progress = { contest.progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (contest.progress > 0.85f) CrimsonPrimary else ElectricCyan,
        trackColor = DarkSurfaceElevated
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Spots Info Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${contest.spotsLeft} spots left",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = CrimsonLight
        )
        Text(
          text = "${contest.totalSpots} spots",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(8.dp))

      // Highlights / Tags
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "1st Prize",
            tint = GoldAmber,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "1st: ₹${formatCurrency(contest.firstPrize)}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldLight
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${contest.winnerPercentage}% Winners",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
        }

        if (contest.isGuaranteed) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, EmeraldGreen)
          ) {
            Text(
              text = "GUARANTEED",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = EmeraldGreen,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

fun formatCurrency(amount: Double): String {
  return when {
    amount >= 10000000.0 -> String.format("%.2f Cr", amount / 10000000.0)
    amount >= 100000.0 -> String.format("%.2f Lakh", amount / 100000.0)
    amount >= 1000.0 -> String.format("%.1f K", amount / 1000.0)
    else -> amount.toInt().toString()
  }
}
