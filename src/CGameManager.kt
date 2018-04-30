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
}

class CGame(iID : Int, iNumberOfTeams : Int) {
    private var m_ID : Int = iID
    private var m_NumberOfTeams : Int by Delegates.notNull()
    private var m_Teams : MutableList<CTeam> by Delegates.notNull()
    private var m_Calendar : CCalendar by Delegates.notNull()
    private val m_TableOfScores : List<Int> = listOf(3, 1, 0)   //  Points for winning, drawing and losing
    init {
        //  Init vars
        m_ID = iID
        m_NumberOfTeams = iNumberOfTeams
        m_Teams = mutableListOf()

        //  Generate team names
        val teamsNames = "A,B,C,D,E,F,G,H,I,J,K,L".split(",")

        //  Generate teams
        (1..m_NumberOfTeams).forEach({
            m_Teams.add(CTeam(teamsNames[it - 1]))
        })

        //  Generate calendar with all matches
        m_Calendar = CCalendar(m_Teams, Options.HomeAndAway)

        //  Points
        m_Calendar.Matches.forEach {
            if(it.ScoreA > it.ScoreB) {
                it.TeamA.Score += m_TableOfScores[0]
                it.TeamA.Won ++
                it.TeamB.Lost ++
            } else if(it.ScoreB > it.ScoreA) {
                it.TeamB.Score += m_TableOfScores[0]
                it.TeamA.Lost ++
                it.TeamB.Won ++
            } else {
                it.TeamA.Score += m_TableOfScores[1]
                it.TeamB.Score += m_TableOfScores[1]
                it.TeamA.Draw ++
                it.TeamB.Draw ++
            }
            it.TeamA.GoalsDone += it.ScoreA
            it.TeamA.GoalsTaken += it.ScoreB
            it.TeamB.GoalsDone += it.ScoreB
            it.TeamB.GoalsTaken += it.ScoreA
        }
    }
    //  *** DATA TYPES ***
    private data class CMatch(val TeamA : CTeam, val TeamB : CTeam, val ScoreA : Int, val ScoreB : Int) { }
    private class CCalendar(iTeams : MutableList<CTeam>, iHomeAndAway : Boolean = false) {
        private var m_Matches : MutableList<CMatch> = mutableListOf()
        private val m_Random = Random()
        private val m_Probabilities = listOf(50, 60, 35, 15, 10, 5, 1)
        private val m_TotalWeight = m_Probabilities.sum()
        init {
            iTeams.forEachIndexed { index, cTeam ->
                iTeams.filterIndexed { otIndex, otTeam -> otIndex > index }.forEach {
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
            var weight = m_Probabilities[index]
            while(rnd >= weight) {
                index ++
                weight += m_Probabilities[index]
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
