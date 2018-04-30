import kotlin.properties.Delegates
import java.util.Random

class CGameManager {
    //  *** PROPERTIES ***
    //  *** MEMBERS ***
    private var m_ID : Int = 0
    private var m_Game : CGame by Delegates.notNull()
    //  *** METHODS ***
    fun NewGame(iNoOfTeams : Int) {
        val id = ++ m_ID
        m_Game = CGame(id, iNoOfTeams)
        println("Generated new game with $iNoOfTeams Teams")
        ShowQuestion()
    }
    fun CurrentGame() {
        println("ID: ${m_Game.ID}, Number of teams: ${m_Game.NumberOfTeams}")
    }
    fun ShowQuestion() {
        m_Game.ShowRanking()
    }
    fun ShowSolution() {
        m_Game.ShowMatches()
    }
    fun ShowGoalWeights() {
        var from = 0
        var to : Int
        Options.GoalWeights.forEachIndexed {
            id, weight ->
            to = from + weight - 1
            println("$id goal -> $weight (from $from to $to)")
            from += weight
        }
    }
}

class CGame(iID : Int, iNumberOfTeams : Int) {
    private val m_ID = iID
    private val m_NumberOfTeams = iNumberOfTeams
    private var m_Teams : MutableList<CTeam> by Delegates.notNull()
    private var m_Calendar : CCalendar by Delegates.notNull()
    private val m_TableOfScores : List<Int> = listOf(3, 1, 0)   //  Points for winning, drawing and losing
    init {
        //  Init vars
        m_Teams = mutableListOf()

        //  Generate team names and teams
        ('A'..'Z').toList().map { it.toString() }.take(iNumberOfTeams).forEach {
            m_Teams.add(CTeam(it))
        }

        //  Generate calendar with all matches
        m_Calendar = CCalendar(m_Teams, Options.HomeAndAway)

        //  Points
        m_Calendar.Matches.forEach {
            when {
                it.ScoreA > it.ScoreB -> {
                    it.TeamA.Score += m_TableOfScores[0]
                    it.TeamA.Won ++
                    it.TeamB.Lost ++
                }
                it.ScoreB > it.ScoreA -> {
                    it.TeamB.Score += m_TableOfScores[0]
                    it.TeamA.Lost ++
                    it.TeamB.Won ++
                }
                else -> {
                    it.TeamA.Score += m_TableOfScores[1]
                    it.TeamB.Score += m_TableOfScores[1]
                    it.TeamA.Draw ++
                    it.TeamB.Draw ++
                }
            }
            it.TeamA.GoalsDone += it.ScoreA
            it.TeamA.GoalsTaken += it.ScoreB
            it.TeamB.GoalsDone += it.ScoreB
            it.TeamB.GoalsTaken += it.ScoreA
        }
    }
    //  *** DATA TYPES ***
    private class CCalendar(iTeams : MutableList<CTeam>, iHomeAndAway : Boolean = false) {
        private var m_Matches : MutableList<CMatch> = mutableListOf()
        private val m_Random = Random()
        private val m_GoalWeights = Options.GoalWeights
        private val m_TotalWeight = m_GoalWeights.sum()
        init {
            iTeams.forEachIndexed { index, cTeam ->
                iTeams.filterIndexed { otIndex, _ -> otIndex > index }.forEach {
                    var results = GenerateScore()
                    m_Matches.add(CMatch(cTeam, it, results.first, results.second))
                    if(iHomeAndAway) {
                        results = GenerateScore()
                        m_Matches.add(CMatch(it, cTeam, results.first, results.second))
                    }
                }
            }
        }
        private fun GenerateScore() : Pair<Int, Int> {
            //  Generates a random number
            return Pair(GeneratesRandomNumber(), GeneratesRandomNumber())
        }
        private fun GeneratesRandomNumber() : Int {
            val rnd = m_Random.nextInt(m_TotalWeight)
            var index = 0
            var weight = m_GoalWeights[index]
            while(rnd >= weight) {
                index ++
                weight += m_GoalWeights[index]
            }
            return index
        }
        val Matches : MutableList<CMatch>
            get() = m_Matches
    }
    //  *** PROPERTIES ***
    val ID : Int
        get() = m_ID
    val NumberOfTeams : Int
        get() = m_NumberOfTeams
    //  *** MEMBERS ***
    //  *** METHODS ***
    fun ShowMatches() {
        m_Calendar.Matches.forEachIndexed {
            id, match ->
            println("Match ${id + 1} : ${match.TeamA.Name} - ${match.TeamB.Name} ${match.ScoreA} - ${match.ScoreB}")
        }
    }
    fun ShowRanking() {
        m_Teams.sortedWith(compareBy({ it.Score})).reversed().forEach {
            val wdl = if(Options.ShowWDL) ", WDL (${it.Won}, ${it.Draw}, ${it.Lost})" else ""
            println("${it.Name} - ${it.Score} points, Goals (${it.GoalsDone}, ${it.GoalsTaken})$wdl")
        }
    }
}
//----------------------------------------------------------------------------------------------------------------------
data class CTeam(
    val Name : String,
    var Score : Int = 0,
    var GoalsDone : Int = 0,
    var GoalsTaken : Int = 0,
    var Won : Int = 0,
    var Draw : Int = 0,
    var Lost : Int = 0
    )
//----------------------------------------------------------------------------------------------------------------------
data class CMatch(
    val TeamA : CTeam,
    val TeamB : CTeam,
    val ScoreA : Int,
    val ScoreB : Int
)
//----------------------------------------------------------------------------------------------------------------------
