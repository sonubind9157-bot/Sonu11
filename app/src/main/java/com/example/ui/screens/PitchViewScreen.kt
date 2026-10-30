package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.model.PlayerRole
import com.example.ui.theme.*

@Composable
fun PitchViewScreen(
  selectedPlayers: List<Player>,
  captainId: String?,
  viceCaptainId: String?,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wkPlayers = selectedPlayers.filter { it.role == PlayerRole.WICKET_KEEPER }
  val batPlayers = selectedPlayers.filter { it.role == PlayerRole.BATSMAN }
  val arPlayers = selectedPlayers.filter { it.role == PlayerRole.ALL_ROUNDER }
  val bowlPlayers = selectedPlayers.filter { it.role == PlayerRole.BOWLER }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 24.dp)
    ) {
      // Top Bar in Pitch View
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Team Pitch Formation",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White
          )
          Text(
            text = "${selectedPlayers.size} Players Selected",
            style = MaterialTheme.typography.labelSmall,
            color = GoldAmber
          )
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .testTag("pitch_close_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Pitch",
            tint = Color.White
          )
        }
      }

      // Realistic Cricket Ground Canvas
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .height(580.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, GoldAmber.copy(alpha = 0.5f))
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(PitchGreenLight, PitchGreenDark)
              )
            )
        ) {
          // Central Cricket Pitch strip (Clay color)
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .width(70.dp)
              .height(280.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(PitchClay.copy(alpha = 0.45f))
              .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
          )

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            // WICKET KEEPERS ROW
            PitchRoleSection(
              title = "WICKET-KEEPERS",
              players = wkPlayers,
              captainId = captainId,
              viceCaptainId = viceCaptainId
            )

            // BATSMEN ROW
            PitchRoleSection(
              title = "BATSMEN",
              players = batPlayers,
              captainId = captainId,
              viceCaptainId = viceCaptainId
            )

            // ALL-ROUNDERS ROW
            PitchRoleSection(
              title = "ALL-ROUNDERS",
              players = arPlayers,
              captainId = captainId,
              viceCaptainId = viceCaptainId
            )

            // BOWLERS ROW
            PitchRoleSection(
              title = "BOWLERS",
              players = bowlPlayers,
              captainId = captainId,
              viceCaptainId = viceCaptainId
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PitchRoleSection(
  title: String,
  players: List<Player>,
  captainId: String?,
  viceCaptainId: String?
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      ),
      color = Color.White.copy(alpha = 0.8f)
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (players.isEmpty()) {
        Text(
          text = "None selected",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = Color.White.copy(alpha = 0.4f)
        )
      } else {
        players.forEach { player ->
          val isC = player.id == captainId
          val isVC = player.id == viceCaptainId

          PitchPlayerBadge(player = player, isCaptain = isC, isViceCaptain = isVC)
        }
      }
    }
  }
}

@Composable
private fun PitchPlayerBadge(
  player: Player,
  isCaptain: Boolean,
  isViceCaptain: Boolean
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(68.dp)
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      // Jersey circle
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(if (player.teamCode == "IND") ElectricCyan else GoldAmber)
          .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SportsCricket,
          contentDescription = "Player",
          tint = DarkBg,
          modifier = Modifier.size(22.dp)
        )
      }

      // C or VC Badge
      if (isCaptain) {
        Box(
          modifier = Modifier
            .offset(x = 6.dp, y = (-4).dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(CrimsonPrimary)
            .border(1.dp, Color.White, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "2X",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 8.sp,
              fontWeight = FontWeight.Black
            ),
            color = Color.White
          )
        }
      } else if (isViceCaptain) {
        Box(
          modifier = Modifier
            .offset(x = 6.dp, y = (-4).dp)
            .size(20.dp)
            .clip(CircleShape)
            .background(ElectricCyan)
            .border(1.dp, Color.White, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "1.5X",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 7.sp,
              fontWeight = FontWeight.Black
            ),
            color = DarkBg
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(2.dp))

    // Player Name Pill
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = DarkBg.copy(alpha = 0.85f),
      border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
    ) {
      Text(
        text = player.name.split(" ").lastOrNull() ?: player.name,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        ),
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
        maxLines = 1
      )
    }

    // Credits / Points
    Text(
      text = "${player.credits} Cr",
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
      color = Color.White.copy(alpha = 0.8f)
    )
  }
}
