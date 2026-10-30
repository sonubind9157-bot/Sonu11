package com.example.model

enum class PlayerRole(val label: String, val shortName: String, val minReq: Int, val maxReq: Int) {
  WICKET_KEEPER("Wicket Keeper", "WK", 1, 4),
  BATSMAN("Batsman", "BAT", 3, 6),
  ALL_ROUNDER("All-Rounder", "AR", 1, 4),
  BOWLER("Bowler", "BOWL", 3, 6)
}

data class Player(
  val id: String,
  val name: String,
  val teamCode: String,
  val role: PlayerRole,
  val credits: Double,
  val points: Int,
  val selectedPercent: Double,
  val isPlaying11Announced: Boolean = true,
  val avatarInitials: String,
  val recentFormScores: List<Int> = emptyList()
)
