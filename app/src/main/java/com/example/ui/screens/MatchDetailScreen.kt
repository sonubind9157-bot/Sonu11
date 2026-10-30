package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contest
import com.example.model.ContestCategory
import com.example.model.Match
import com.example.ui.components.ContestItemCard
import com.example.ui.theme.*

@Composable
fun MatchDetailScreen(
  match: Match,
  contests: List<Contest>,
  selectedPlayerCount: Int,
  userTeamCount: Int,
  onJoinContest: (Contest) -> Unit,
  onCreateOrEditTeamClick: () -> Unit,
  onPitchPreviewClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<ContestCategory?>(null) } // null = All
  var viewMode by remember { mutableStateOf(0) } // 0: Contests, 1: Joined Contests

  val filteredContests = remember(selectedCategory, contests, viewMode) {
    var list = if (viewMode == 1) contests.filter { it.isJoined } else contests
    if (selectedCategory != null) {
      list = list.filter { it.category == selectedCategory }
    }
    list
  }

  Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 100.dp)
    ) {
      // Match Top Summary Header
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${match.seriesName} • ${match.matchNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = "Time Left",
                  tint = GoldAmber,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = match.timeRemainingText,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = GoldAmber
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score / Teams
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = match.team1FlagEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = match.team1Code,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                  )
                  Text(text = match.team1Name, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
              }

              Text(
                text = "VS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextMuted
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = match.team2Code,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                  )
                  Text(text = match.team2Name, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = match.team2FlagEmoji, fontSize = 28.sp)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "📍 ${match.venue}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
              )
              Text(
                text = "Mega Pool: ${match.megaPrizePool}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = GoldAmber
              )
            }
          }
        }
      }

      // Mode Switch: All Contests vs Joined Contests
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.weight(1f),
            color = Color.Transparent
          )
          Button(
            onClick = { viewMode = 0 },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (viewMode == 0) CrimsonPrimary else DarkSurface
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("contests_tab_all")
          ) {
            Text(
              text = "Contests (${contests.size})",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Button(
            onClick = { viewMode = 1 },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (viewMode == 1) CrimsonPrimary else DarkSurface
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("contests_tab_joined")
          ) {
            Text(
              text = "My Contests (${contests.count { it.isJoined }})",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }

      // Filter Categories Chips
      if (viewMode == 0) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = selectedCategory == null,
              onClick = { selectedCategory = null },
              label = { Text("All") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricCyan,
                selectedLabelColor = DarkBg,
                containerColor = DarkSurface,
                labelColor = TextSecondary
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedCategory == null,
                borderColor = DarkSurfaceBorder,
                selectedBorderColor = ElectricCyan
              )
            )

            listOf(
              ContestCategory.MEGA to "Mega",
              ContestCategory.HEAD_TO_HEAD to "H2H",
              ContestCategory.WINNER_TAKES_ALL to "Winner Takes All",
              ContestCategory.PRACTICE to "Practice"
            ).forEach { (cat, label) ->
              FilterChip(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ElectricCyan,
                  selectedLabelColor = DarkBg,
                  containerColor = DarkSurface,
                  labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = selectedCategory == cat,
                  borderColor = DarkSurfaceBorder,
                  selectedBorderColor = ElectricCyan
                )
              )
            }
          }
        }
      }

      // Contests List
      if (filteredContests.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (viewMode == 1) "You have not joined any contests yet." else "No contests match criteria",
              style = MaterialTheme.typography.bodyMedium,
              color = TextMuted
            )
          }
        }
      } else {
        items(filteredContests, key = { it.id }) { contest ->
          ContestItemCard(
            contest = contest,
            onJoinClick = { onJoinContest(contest) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }
      }
    }

    // Sticky Bottom Action Bar: Team Builder / Pitch Preview
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
        // Pitch View preview button
        OutlinedButton(
          onClick = onPitchPreviewClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
          modifier = Modifier.testTag("bottom_pitch_preview_btn")
        ) {
          Icon(
            imageVector = Icons.Default.SportsCricket,
            contentDescription = "Pitch View",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Pitch View", fontWeight = FontWeight.Bold)
        }

        // Create / Edit Team Button
        Button(
          onClick = onCreateOrEditTeamClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
          modifier = Modifier
            .weight(1f)
            .testTag("bottom_create_team_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = "Team",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (selectedPlayerCount == 11) "Edit Team ($selectedPlayerCount/11)" else "Create Team ($selectedPlayerCount/11)",
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
