package com.example.model

data class FantasyTeam(
  val id: String,
  val matchId: String,
  val teamName: String,
  val selectedPlayers: List<Player>,
  val captainId: String,
  val viceCaptainId: String,
  val totalCreditsUsed: Double,
  val createdTimestamp: Long = System.currentTimeMillis()
) {
  val captain: Player? get() = selectedPlayers.find { it.id == captainId }
  val viceCaptain: Player? get() = selectedPlayers.find { it.id == viceCaptainId }
  
  fun getRoleCount(role: PlayerRole): Int = selectedPlayers.count { it.role == role }
  fun getTeamCount(teamCode: String): Int = selectedPlayers.count { it.teamCode == teamCode }
}
