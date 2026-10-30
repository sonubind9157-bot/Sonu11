package com.example.model

enum class ContestCategory {
  MEGA,
  HEAD_TO_HEAD,
  WINNER_TAKES_ALL,
  PRACTICE
}

data class Contest(
  val id: String,
  val matchId: String,
  val title: String,
  val category: ContestCategory,
  val prizePool: Double,
  val entryFee: Double,
  val totalSpots: Int,
  val spotsFilled: Int,
  val firstPrize: Double,
  val winnerPercentage: Int,
  val maxTeamsPerUser: Int = 11,
  val isGuaranteed: Boolean = true,
  val isJoined: Boolean = false,
  val joinedTeamName: String? = null
) {
  val isFull: Boolean get() = spotsFilled >= totalSpots
  val spotsLeft: Int get() = (totalSpots - spotsFilled).coerceAtLeast(0)
  val progress: Float get() = if (totalSpots > 0) spotsFilled.toFloat() / totalSpots else 0f
}
