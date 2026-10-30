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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.theme.*

@Composable
fun CaptainSelectionScreen(
  selectedPlayers: List<Player>,
  captainId: String?,
  viceCaptainId: String?,
  onSetCaptain: (String) -> Unit,
  onSetViceCaptain: (String) -> Unit,
  onSaveTeamClick: () -> Unit,
  onPitchPreviewClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header Info Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Choose Your Captain & Vice-Captain",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(GoldAmber),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "C",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = DarkBg
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Captain gets 2X Points",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAmber
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(ElectricCyan),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "VC",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = DarkBg
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VC gets 1.5X Points",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan
              )
            }
          }
        }
      }

      // Selected Players Table Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PLAYER",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = TextMuted,
          modifier = Modifier.weight(1f)
        )
        Text(
          text = "% C BY",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = TextMuted,
          modifier = Modifier.width(55.dp)
        )
        Text(
          text = "% VC BY",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = TextMuted,
          modifier = Modifier.width(55.dp)
        )
      }

      // Players List
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentPadding = PaddingValues(bottom = 90.dp)
      ) {
        items(selectedPlayers, key = { it.id }) { player ->
          val isC = captainId == player.id
          val isVC = viceCaptainId == player.id

          Column {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Player Avatar
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(if (player.teamCode == "IND") ElectricCyan.copy(alpha = 0.2f) else GoldAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = player.avatarInitials,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              // Player Name & Role
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = player.name,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
                Text(
                  text = "${player.teamCode} • ${player.role.shortName} • ${player.credits} Cr",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMuted
                )
              }

              // Captain Selector Button
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(if (isC) GoldAmber else DarkSurfaceElevated)
                  .border(1.dp, if (isC) GoldAmber else DarkSurfaceBorder, CircleShape)
                  .clickable { onSetCaptain(player.id) }
                  .testTag("captain_btn_${player.id}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "C",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                  color = if (isC) DarkBg else Color.White
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              // Vice Captain Selector Button
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(if (isVC) ElectricCyan else DarkSurfaceElevated)
                  .border(1.dp, if (isVC) ElectricCyan else DarkSurfaceBorder, CircleShape)
                  .clickable { onSetViceCaptain(player.id) }
                  .testTag("vice_captain_btn_${player.id}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "VC",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                  color = if (isVC) DarkBg else Color.White
                )
              }
            }

            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
          }
        }
      }
    }

    // Bottom Action Bar
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
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
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
          onClick = onSaveTeamClick,
          enabled = captainId != null && viceCaptainId != null,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldGreen,
            disabledContainerColor = DarkSurfaceBorder
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("save_fantasy_team_btn")
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Save",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save & Join Contests", fontWeight = FontWeight.Black)
        }
      }
    }
  }
}
