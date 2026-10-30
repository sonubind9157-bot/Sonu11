package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Match
import com.example.model.Player
import com.example.ui.theme.*

data class LivePlayerPerformance(
  val player: Player,
  val liveRuns: Int,
  val liveBalls: Int,
  val liveWickets: Int,
  val liveOvers: Double,
  val liveCatches: Int,
  val matchFantasyPts: Int,
  val isCaptain: Boolean = false,
  val isViceCaptain: Boolean = false
)

@Composable
fun LiveMatchScreen(
  match: Match,
  selectedPlayers: List<Player>,
  captainId: String?,
  viceCaptainId: String?,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Mock dynamic performance stats for the live match
  val performances = selectedPlayers.mapIndexed { idx, p ->
    val isC = p.id == captainId
    val isVC = p.id == viceCaptainId
    val base = (p.points / 3) + (idx * 7)
    val total = if (isC) base * 2 else if (isVC) (base * 1.5).toInt() else base
    LivePlayerPerformance(
      player = p,
      liveRuns = if (p.role.name.contains("BAT") || p.role.name.contains("ALL")) 38 + (idx * 5) else 8,
      liveBalls = 24 + idx,
      liveWickets = if (p.role.name.contains("BOWL") || p.role.name.contains("ALL")) (idx % 3) else 0,
      liveOvers = 3.4,
      liveCatches = if (idx % 4 == 0) 1 else 0,
      matchFantasyPts = total,
      isCaptain = isC,
      isViceCaptain = isVC
    )
  }

  val totalTeamLivePoints = performances.sumOf { it.matchFantasyPts }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
      // Live Scorecard Hero Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("live_scorecard_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonPrimary)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(CrimsonPrimary)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "LIVE INNINGS 2",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
              }

              Text(
                text = match.seriesName,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scores
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "${match.team1Code}  ${match.liveTeam1Score ?: "188/4"}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
                Text(
                  text = match.team1Name,
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMuted
                )
              }

              Text(text = "vs", color = TextMuted, fontWeight = FontWeight.Bold)

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "${match.team2Code}  ${match.liveTeam2Score ?: "142/3"}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = ElectricCyan
                )
                Text(
                  text = "${match.team2Name} (Batting)",
                  style = MaterialTheme.typography.labelSmall,
                  color = ElectricCyan
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = match.liveStatusNote ?: "MI need 47 runs in 28 balls to win",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = GoldAmber
            )
          }
        }
      }

      // Fantasy Live Leaderboard Tracker Card
      item {
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "YOUR TEAM LIVE POINTS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                ),
                color = TextSecondary
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "$totalTeamLivePoints",
                  style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                  color = GoldAmber
                )
                Text(
                  text = " pts",
                  style = MaterialTheme.typography.titleSmall,
                  color = TextMuted
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "LIVE RANK & WINNINGS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TextSecondary
              )
              Text(
                text = "Rank #42 / 10,000",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = EmeraldGreen
              )
              Text(
                text = "Projected Prize: ₹1,500",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
              )
            }
          }
        }
      }

      // Player Breakdown Header
      item {
        Spacer(modifier = Modifier.height(18.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Player Points Breakdown",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Text(
            text = "Live Updates On",
            style = MaterialTheme.typography.labelSmall,
            color = EmeraldGreen
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // Player Performance List
      items(performances, key = { it.player.id }) { perf ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(contentAlignment = Alignment.TopEnd) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (perf.player.teamCode == "IND") ElectricCyan.copy(alpha = 0.2f) else GoldAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = perf.player.avatarInitials,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
              }
              if (perf.isCaptain) {
                Box(
                  modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(GoldAmber),
                  contentAlignment = Alignment.Center
                ) {
                  Text("C", fontSize = 8.sp, fontWeight = FontWeight.Black, color = DarkBg)
                }
              } else if (perf.isViceCaptain) {
                Box(
                  modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan),
                  contentAlignment = Alignment.Center
                ) {
                  Text("VC", fontSize = 7.sp, fontWeight = FontWeight.Black, color = DarkBg)
                }
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = perf.player.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "${perf.liveRuns} runs (${perf.liveBalls}b) • ${perf.liveWickets} wkts • ${perf.liveCatches} ct",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
            }

            Text(
              text = "+${perf.matchFantasyPts} pts",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = if (perf.isCaptain || perf.isViceCaptain) GoldAmber else Color.White
            )
          }
        }
      }
    }
  }
}
