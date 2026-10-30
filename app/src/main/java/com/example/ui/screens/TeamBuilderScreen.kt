package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.model.PlayerRole
import com.example.ui.theme.*

@Composable
fun TeamBuilderScreen(
  match: Match,
  allPlayers: List<Player>,
  selectedPlayerIds: Set<String>,
  selectedRole: PlayerRole,
  onRoleSelected: (PlayerRole) -> Unit,
  onTogglePlayer: (Player) -> Unit,
  onPitchPreviewClick: () -> Unit,
  onNextCaptainClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedPlayers = remember(selectedPlayerIds, allPlayers) {
    allPlayers.filter { selectedPlayerIds.contains(it.id) }
  }

  val totalCreditsUsed = remember(selectedPlayers) {
    selectedPlayers.sumOf { it.credits }
  }
  val creditsLeft = (100.0 - totalCreditsUsed).coerceAtLeast(0.0)

  val team1Count = remember(selectedPlayers, match.team1Code) {
    selectedPlayers.count { it.teamCode == match.team1Code }
  }
  val team2Count = remember(selectedPlayers, match.team2Code) {
    selectedPlayers.count { it.teamCode == match.team2Code }
  }

  val roleFilteredPlayers = remember(selectedRole, allPlayers) {
    allPlayers.filter { it.role == selectedRole }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Metric Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Team counts & credits left
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Players",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${selectedPlayers.size}",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                  color = if (selectedPlayers.size == 11) EmeraldGreen else Color.White
                )
                Text(
                  text = " / 11",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextMuted
                )
              }
            }

            // Team Distribution
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${match.team1Code}: $team1Count",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "•", color = TextMuted)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${match.team2Code}: $team2Count",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldAmber
              )
            }

            // Credits Left
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Credits Left",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Text(
                text = String.format("%.1f", creditsLeft),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = if (creditsLeft < 9.0 && selectedPlayers.size < 11) CrimsonPrimary else GoldAmber
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 11 Indicator dots
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            for (i in 0 until 11) {
              val isFilled = i < selectedPlayers.size
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(5.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(if (isFilled) EmeraldGreen else DarkSurfaceBorder)
              )
            }
          }
        }
      }

      // Role Tabs (WK, BAT, AR, BOWL)
      TabRow(
        selectedTabIndex = PlayerRole.values().indexOf(selectedRole),
        containerColor = DarkSurface,
        contentColor = CrimsonPrimary,
        divider = { HorizontalDivider(color = DarkSurfaceBorder) }
      ) {
        PlayerRole.values().forEach { role ->
          val countInRole = selectedPlayers.count { it.role == role }
          Tab(
            selected = selectedRole == role,
            onClick = { onRoleSelected(role) },
            text = {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${role.shortName} ($countInRole)",
                  fontWeight = if (selectedRole == role) FontWeight.Black else FontWeight.SemiBold,
                  fontSize = 12.sp,
                  color = if (selectedRole == role) CrimsonPrimary else TextSecondary
                )
                Text(
                  text = "(${role.minReq}-${role.maxReq})",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = TextMuted
                )
              }
            },
            modifier = Modifier.testTag("role_tab_${role.name}")
          )
        }
      }

      // Player List
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentPadding = PaddingValues(bottom = 90.dp)
      ) {
        item {
          // Table Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(DarkSurfaceElevated.copy(alpha = 0.5f))
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PLAYER",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = TextMuted,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "POINTS",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = TextMuted,
              modifier = Modifier.width(60.dp)
            )
            Text(
              text = "CREDITS",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = TextMuted,
              modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(44.dp))
          }
        }

        items(roleFilteredPlayers, key = { it.id }) { player ->
          val isSelected = selectedPlayerIds.contains(player.id)
          PlayerListItem(
            player = player,
            isSelected = isSelected,
            onToggle = { onTogglePlayer(player) }
          )
        }
      }
    }

    // Bottom Sticky Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding(),
      color = DarkSurfaceElevated,
      tonalElevation = 8.dp,
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onPitchPreviewClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
          modifier = Modifier.testTag("team_pitch_preview_btn")
        ) {
          Icon(
            imageVector = Icons.Default.SportsCricket,
            contentDescription = "Pitch View",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Pitch", fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onNextCaptainClick,
          enabled = selectedPlayers.size == 11,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = CrimsonPrimary,
            disabledContainerColor = DarkSurfaceBorder
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("team_next_captain_btn")
        ) {
          Text(
            text = if (selectedPlayers.size == 11) "Continue (Choose C/VC)" else "Select 11 Players (${selectedPlayers.size}/11)",
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Next",
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun PlayerListItem(
  player: Player,
  isSelected: Boolean,
  onToggle: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onToggle)
      .testTag("player_row_${player.id}"),
    color = if (isSelected) CrimsonPrimary.copy(alpha = 0.08f) else DarkBg
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Player Avatar with Playing 11 Badge
        Box(contentAlignment = Alignment.BottomEnd) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(if (player.teamCode == "IND") ElectricCyan.copy(alpha = 0.25f) else GoldAmber.copy(alpha = 0.25f))
              .border(
                1.dp,
                if (player.teamCode == "IND") ElectricCyan else GoldAmber,
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = player.avatarInitials,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
              color = Color.White
            )
          }

          if (player.isPlaying11Announced) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(EmeraldGreen)
                .border(1.5.dp, DarkBg, CircleShape)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name & Info
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = player.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = player.teamCode,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (player.teamCode == "IND") ElectricCyan else GoldAmber
            )
            Text(
              text = " • Sel by ${player.selectedPercent}%",
              style = MaterialTheme.typography.labelSmall,
              color = TextMuted
            )
          }
        }

        // Fantasy Points
        Text(
          text = "${player.points}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = Color.White,
          modifier = Modifier.width(60.dp)
        )

        // Credits Cost
        Text(
          text = "${player.credits}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
          color = GoldAmber,
          modifier = Modifier.width(60.dp)
        )

        // Add/Remove Action Button
        IconButton(
          onClick = onToggle,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) CrimsonPrimary else DarkSurfaceElevated)
        ) {
          Icon(
            imageVector = if (isSelected) Icons.Default.Remove else Icons.Default.Add,
            contentDescription = if (isSelected) "Remove Player" else "Add Player",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
  }
}
