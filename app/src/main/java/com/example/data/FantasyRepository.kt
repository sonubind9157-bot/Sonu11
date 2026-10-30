package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FantasyRepository {

  private val _wallet = MutableStateFlow(
    Wallet(
      totalBalance = 1450.0,
      unutilizedAmount = 600.0,
      winningsAmount = 750.0,
      cashBonus = 100.0
    )
  )
  val wallet: StateFlow<Wallet> = _wallet.asStateFlow()

  private val _transactions = MutableStateFlow<List<Transaction>>(generateInitialTransactions())
  val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

  private val _matches = MutableStateFlow<List<Match>>(generateMatches())
  val matches: StateFlow<List<Match>> = _matches.asStateFlow()

  private val _contests = MutableStateFlow<Map<String, List<Contest>>>(generateContestsMap())
  val contests: StateFlow<Map<String, List<Contest>>> = _contests.asStateFlow()

  private val _players = MutableStateFlow<Map<String, List<Player>>>(generatePlayersMap())
  val players: StateFlow<Map<String, List<Player>>> = _players.asStateFlow()

  private val _userTeams = MutableStateFlow<List<FantasyTeam>>(emptyList())
  val userTeams: StateFlow<List<FantasyTeam>> = _userTeams.asStateFlow()

  // Coupon promo codes: Code -> Discount or bonus amount
  val promoCodes = mapOf(
    "FIRST100" to 100.0,
    "IPL2026" to 150.0,
    "FANTASY50" to 50.0,
    "MEGABONUS" to 200.0
  )

  fun addCashDeposit(
    amount: Double,
    paymentMethod: PaymentMethod,
    referenceId: String,
    couponApplied: String? = null
  ): Transaction {
    val bonus = if (couponApplied != null) promoCodes[couponApplied] ?: 0.0 else 0.0
    val current = _wallet.value
    val newWallet = current.copy(
      totalBalance = current.totalBalance + amount + bonus,
      unutilizedAmount = current.unutilizedAmount + amount,
      cashBonus = current.cashBonus + bonus
    )
    _wallet.value = newWallet

    val tx = Transaction(
      id = "TXN_" + UUID.randomUUID().toString().take(8).uppercase(),
      type = TransactionType.DEPOSIT,
      amount = amount,
      status = TransactionStatus.SUCCESS,
      timestamp = System.currentTimeMillis(),
      paymentMethod = paymentMethod,
      referenceNumber = referenceId,
      title = "Cash Deposit",
      description = "Wallet loaded via ${paymentMethod.displayName}" +
          if (bonus > 0) " (Bonus +₹${bonus.toInt()} credited)" else ""
    )
    _transactions.value = listOf(tx) + _transactions.value
    return tx
  }

  fun withdrawWinnings(amount: Double, destination: String): Pair<Boolean, String> {
    val current = _wallet.value
    if (amount > current.winningsAmount) {
      return Pair(false, "Withdrawal amount cannot exceed withdrawable winnings (₹${current.winningsAmount})")
    }
    if (amount < 100.0) {
      return Pair(false, "Minimum withdrawal limit is ₹100")
    }

    val newWallet = current.copy(
      totalBalance = current.totalBalance - amount,
      winningsAmount = current.winningsAmount - amount
    )
    _wallet.value = newWallet

    val tx = Transaction(
      id = "WTH_" + UUID.randomUUID().toString().take(8).uppercase(),
      type = TransactionType.WITHDRAWAL,
      amount = amount,
      status = TransactionStatus.SUCCESS,
      timestamp = System.currentTimeMillis(),
      referenceNumber = "IMPS" + System.currentTimeMillis().toString().takeLast(10),
      title = "Bank Withdrawal",
      description = "Transferred to $destination",
      bankOrUpiAccount = destination
    )
    _transactions.value = listOf(tx) + _transactions.value
    return Pair(true, "₹${amount.toInt()} successfully transferred via Instant IMPS!")
  }

  fun joinContest(contestId: String, matchId: String, team: FantasyTeam): Pair<Boolean, String> {
    val contestList = _contests.value[matchId] ?: return Pair(false, "Match not found")
    val contest = contestList.find { it.id == contestId } ?: return Pair(false, "Contest not found")

    if (contest.isFull) {
      return Pair(false, "Contest is already full")
    }

    // Check entry fee deduction: Can use up to 10% bonus
    val current = _wallet.value
    val fee = contest.entryFee

    if (fee > 0) {
      val bonusUsable = (fee * 0.1).coerceAtMost(current.cashBonus)
      val realCashNeeded = fee - bonusUsable

      if (realCashNeeded > (current.unutilizedAmount + current.winningsAmount)) {
        return Pair(false, "INSUFFICIENT_FUNDS")
      }

      var remainingToDeduct = realCashNeeded
      val deductFromUnutilized = remainingToDeduct.coerceAtMost(current.unutilizedAmount)
      remainingToDeduct -= deductFromUnutilized
      val deductFromWinnings = remainingToDeduct

      val updatedWallet = current.copy(
        totalBalance = current.totalBalance - fee,
        unutilizedAmount = current.unutilizedAmount - deductFromUnutilized,
        winningsAmount = current.winningsAmount - deductFromWinnings,
        cashBonus = current.cashBonus - bonusUsable
      )
      _wallet.value = updatedWallet

      val tx = Transaction(
        id = "ENT_" + UUID.randomUUID().toString().take(8).uppercase(),
        type = TransactionType.CONTEST_ENTRY,
        amount = fee,
        status = TransactionStatus.SUCCESS,
        timestamp = System.currentTimeMillis(),
        referenceNumber = "CONTEST_${contest.id}",
        title = "Joined ${contest.title}",
        description = "Entry fee for ${contest.category.name} (${team.teamName})"
      )
      _transactions.value = listOf(tx) + _transactions.value
    }

    // Update contest spots
    val updatedContests = contestList.map {
      if (it.id == contestId) {
        it.copy(
          spotsFilled = it.spotsFilled + 1,
          isJoined = true,
          joinedTeamName = team.teamName
        )
      } else it
    }
    val allContests = _contests.value.toMutableMap()
    allContests[matchId] = updatedContests
    _contests.value = allContests

    return Pair(true, "Contest successfully joined with ${team.teamName}!")
  }

  fun saveFantasyTeam(team: FantasyTeam) {
    _userTeams.value = _userTeams.value.filterNot { it.id == team.id } + team
  }

  private fun generateInitialTransactions(): List<Transaction> {
    return listOf(
      Transaction(
        id = "TXN_78491201",
        type = TransactionType.CONTEST_WINNINGS,
        amount = 750.0,
        status = TransactionStatus.SUCCESS,
        timestamp = System.currentTimeMillis() - 86400000L,
        referenceNumber = "WIN_IND_ENG_01",
        title = "Mega Contest Rank #3 Winnings",
        description = "India vs England 1st T20 - Team 1 scored 482 pts"
      ),
      Transaction(
        id = "TXN_78490014",
        type = TransactionType.DEPOSIT,
        amount = 500.0,
        status = TransactionStatus.SUCCESS,
        timestamp = System.currentTimeMillis() - 172800000L,
        paymentMethod = PaymentMethod.UPI,
        referenceNumber = "UPI98234019230",
        title = "Add Cash (Google Pay)",
        description = "Wallet loaded via UPI instant transfer"
      ),
      Transaction(
        id = "TXN_78489111",
        type = TransactionType.BONUS_REWARD,
        amount = 100.0,
        status = TransactionStatus.SUCCESS,
        timestamp = System.currentTimeMillis() - 259200000L,
        referenceNumber = "BONUS_SIGNUP",
        title = "Welcome Bonus",
        description = "Special new player sign-up reward"
      )
    )
  }

  private fun generateMatches(): List<Match> {
    return listOf(
      Match(
        id = "match_ind_aus",
        seriesName = "T20 Super Cup 2026",
        matchNumber = "1st T20I",
        team1Code = "IND",
        team1Name = "India",
        team1FlagEmoji = "🇮🇳",
        team2Code = "AUS",
        team2Name = "Australia",
        team2FlagEmoji = "🇦🇺",
        startTimeText = "Today, 7:30 PM",
        timeRemainingText = "02h 45m left",
        venue = "Wankhede Stadium, Mumbai",
        status = MatchStatus.UPCOMING,
        megaPrizePool = "₹2.50 Crore"
      ),
      Match(
        id = "match_csk_mi",
        seriesName = "Indian T20 Premier League",
        matchNumber = "Match 24",
        team1Code = "CSK",
        team1Name = "Chennai Kings",
        team1FlagEmoji = "🦁",
        team2Code = "MI",
        team2Name = "Mumbai Champions",
        team2FlagEmoji = "⚡",
        startTimeText = "LIVE NOW",
        timeRemainingText = "Live (Innings 2)",
        venue = "M. A. Chidambaram Stadium, Chennai",
        status = MatchStatus.LIVE,
        megaPrizePool = "₹5.00 Crore",
        liveTeam1Score = "CSK 188/4 (20 ov)",
        liveTeam2Score = "MI 142/3 (15.2 ov)",
        liveStatusNote = "MI need 47 runs in 28 balls"
      ),
      Match(
        id = "match_rcb_kkr",
        seriesName = "Indian T20 Premier League",
        matchNumber = "Match 25",
        team1Code = "RCB",
        team1Name = "Bangalore Royals",
        team1FlagEmoji = "🔴",
        team2Code = "KKR",
        team2Name = "Kolkata Knights",
        team2FlagEmoji = "🟣",
        startTimeText = "Tomorrow, 3:30 PM",
        timeRemainingText = "19h 20m left",
        venue = "M. Chinnaswamy Stadium, Bengaluru",
        status = MatchStatus.UPCOMING,
        megaPrizePool = "₹3.50 Crore"
      ),
      Match(
        id = "match_eng_sa",
        seriesName = "World Champions Trophy",
        matchNumber = "Semi-Final 2",
        team1Code = "ENG",
        team1Name = "England",
        team1FlagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
        team2Code = "SA",
        team2Name = "South Africa",
        team2FlagEmoji = "🇿🇦",
        startTimeText = "Completed",
        timeRemainingText = "Result Declared",
        venue = "Lord's, London",
        status = MatchStatus.COMPLETED,
        megaPrizePool = "₹1.80 Crore",
        liveTeam1Score = "ENG 210/6",
        liveTeam2Score = "SA 192/9",
        liveStatusNote = "ENG won by 18 runs"
      )
    )
  }

  private fun generateContestsMap(): Map<String, List<Contest>> {
    return mapOf(
      "match_ind_aus" to listOf(
        Contest(
          id = "c_mega_1",
          matchId = "match_ind_aus",
          title = "Mega Contest (Guaranteed)",
          category = ContestCategory.MEGA,
          prizePool = 25000000.0, // 2.5 Cr
          entryFee = 49.0,
          totalSpots = 650000,
          spotsFilled = 482190,
          firstPrize = 2500000.0, // 25 Lakhs
          winnerPercentage = 68,
          isGuaranteed = true
        ),
        Contest(
          id = "c_h2h_1",
          matchId = "match_ind_aus",
          title = "Head-to-Head Double Up",
          category = ContestCategory.HEAD_TO_HEAD,
          prizePool = 10000.0,
          entryFee = 575.0,
          totalSpots = 2,
          spotsFilled = 1,
          firstPrize = 10000.0,
          winnerPercentage = 50,
          isGuaranteed = true
        ),
        Contest(
          id = "c_wta_1",
          matchId = "match_ind_aus",
          title = "Winner Takes All (4 Spots)",
          category = ContestCategory.WINNER_TAKES_ALL,
          prizePool = 1000.0,
          entryFee = 299.0,
          totalSpots = 4,
          spotsFilled = 3,
          firstPrize = 1000.0,
          winnerPercentage = 25,
          isGuaranteed = true
        ),
        Contest(
          id = "c_budget_1",
          matchId = "match_ind_aus",
          title = "Mini Mega Contest",
          category = ContestCategory.MEGA,
          prizePool = 500000.0,
          entryFee = 19.0,
          totalSpots = 35000,
          spotsFilled = 28100,
          firstPrize = 50000.0,
          winnerPercentage = 65,
          isGuaranteed = true
        ),
        Contest(
          id = "c_practice_1",
          matchId = "match_ind_aus",
          title = "Skill Practice Arena",
          category = ContestCategory.PRACTICE,
          prizePool = 0.0,
          entryFee = 0.0,
          totalSpots = 10000,
          spotsFilled = 6420,
          firstPrize = 0.0,
          winnerPercentage = 100,
          isGuaranteed = true
        )
      ),
      "match_csk_mi" to listOf(
        Contest(
          id = "c_csk_mega",
          matchId = "match_csk_mi",
          title = "El Clásico Mega Clash",
          category = ContestCategory.MEGA,
          prizePool = 50000000.0,
          entryFee = 59.0,
          totalSpots = 1000000,
          spotsFilled = 980120,
          firstPrize = 5000000.0,
          winnerPercentage = 70,
          isGuaranteed = true,
          isJoined = true,
          joinedTeamName = "SuperStrikers 1"
        )
      )
    )
  }

  private fun generatePlayersMap(): Map<String, List<Player>> {
    // 22 real cricketers for IND vs AUS
    val indAusPlayers = listOf(
      // Wicket Keepers (WK)
      Player("p_kl", "KL Rahul", "IND", PlayerRole.WICKET_KEEPER, 9.0, 142, 84.5, true, "KL", listOf(68, 45, 82)),
      Player("p_inglis", "Josh Inglis", "AUS", PlayerRole.WICKET_KEEPER, 8.5, 98, 42.1, true, "JI", listOf(38, 52, 19)),
      Player("p_pant", "Rishabh Pant", "IND", PlayerRole.WICKET_KEEPER, 9.0, 120, 68.3, true, "RP", listOf(74, 31, 55)),
      Player("p_carey", "Alex Carey", "AUS", PlayerRole.WICKET_KEEPER, 8.0, 64, 21.0, true, "AC", listOf(18, 42, 27)),

      // Batsmen (BAT)
      Player("p_rohit", "Rohit Sharma", "IND", PlayerRole.BATSMAN, 9.5, 185, 89.2, true, "RS", listOf(85, 92, 44)),
      Player("p_virat", "Virat Kohli", "IND", PlayerRole.BATSMAN, 10.0, 210, 93.8, true, "VK", listOf(112, 65, 88)),
      Player("p_head", "Travis Head", "AUS", PlayerRole.BATSMAN, 9.5, 198, 88.0, true, "TH", listOf(78, 104, 36)),
      Player("p_sky", "Suryakumar Yadav", "IND", PlayerRole.BATSMAN, 9.0, 160, 77.4, true, "SK", listOf(64, 48, 83)),
      Player("p_marsh", "Mitchell Marsh", "AUS", PlayerRole.BATSMAN, 9.0, 155, 71.9, true, "MM", listOf(54, 72, 41)),
      Player("p_david", "Tim David", "AUS", PlayerRole.BATSMAN, 8.5, 94, 38.6, true, "TD", listOf(42, 18, 56)),
      Player("p_jaiswal", "Yashasvi Jaiswal", "IND", PlayerRole.BATSMAN, 9.0, 148, 64.2, true, "YJ", listOf(90, 32, 60)),

      // All Rounders (AR)
      Player("p_hardik", "Hardik Pandya", "IND", PlayerRole.ALL_ROUNDER, 9.5, 180, 87.6, true, "HP", listOf(55, 68, 79)),
      Player("p_maxwell", "Glenn Maxwell", "AUS", PlayerRole.ALL_ROUNDER, 9.0, 165, 81.3, true, "GM", listOf(46, 85, 62)),
      Player("p_axar", "Axar Patel", "IND", PlayerRole.ALL_ROUNDER, 8.5, 130, 62.0, true, "AP", listOf(38, 54, 45)),
      Player("p_stoinis", "Marcus Stoinis", "AUS", PlayerRole.ALL_ROUNDER, 8.5, 122, 58.4, true, "MS", listOf(34, 60, 48)),

      // Bowlers (BOWL)
      Player("p_bumrah", "Jasprit Bumrah", "IND", PlayerRole.BOWLER, 9.5, 205, 94.2, true, "JB", listOf(88, 95, 110)),
      Player("p_cummins", "Pat Cummins", "AUS", PlayerRole.BOWLER, 9.0, 150, 76.5, true, "PC", listOf(62, 58, 74)),
      Player("p_starc", "Mitchell Starc", "AUS", PlayerRole.BOWLER, 9.0, 168, 80.2, true, "MSt", listOf(70, 82, 65)),
      Player("p_arshdeep", "Arshdeep Singh", "IND", PlayerRole.BOWLER, 8.5, 138, 69.1, true, "AS", listOf(52, 74, 48)),
      Player("p_zampa", "Adam Zampa", "AUS", PlayerRole.BOWLER, 8.5, 144, 72.8, true, "AZ", listOf(68, 75, 59)),
      Player("p_kuldeep", "Kuldeep Yadav", "IND", PlayerRole.BOWLER, 8.5, 132, 65.3, true, "KY", listOf(64, 45, 80)),
      Player("p_hazlewood", "Josh Hazlewood", "AUS", PlayerRole.BOWLER, 8.5, 125, 59.7, true, "JH", listOf(48, 62, 55))
    )

    return mapOf(
      "match_ind_aus" to indAusPlayers,
      "match_csk_mi" to indAusPlayers // Reusable roster structure with relevant players
    )
  }
}
