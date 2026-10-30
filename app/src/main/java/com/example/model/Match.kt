package com.example.model

enum class MatchStatus {
  UPCOMING,
  LIVE,
  COMPLETED
}

data class Match(
  val id: String,
  val seriesName: String,
  val matchNumber: String,
  val team1Code: String,
  val team1Name: String,
  val team1FlagEmoji: String,
  val team2Code: String,
  val team2Name: String,
  val team2FlagEmoji: String,
  val startTimeText: String,
  val timeRemainingText: String,
  val venue: String,
  val status: MatchStatus,
  val megaPrizePool: String,
  val liveTeam1Score: String? = null,
  val liveTeam2Score: String? = null,
  val liveStatusNote: String? = null
)
