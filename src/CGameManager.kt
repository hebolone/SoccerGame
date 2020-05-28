import java.io.Serializable
import java.util.Random

class CGameManager {
    //  *** DATA TYPES ***
    private class CCalendar(iTeams : MutableList<CTeam>, iHomeAndAway : Boolean = false) {
        private val m_Matches : MutableList<CMatch> = mutableListOf()
        private val m_Random = Random()
        private val m_GoalWeights = Options.GoalWeights
        private val m_TotalWeight = m_GoalWeights.sum()
        private val m_TableOfScores = mapOf("WON" to 3, "DRAWN" to 1, "LOST" to 0)
        init {
            iTeams.forEachIndexed { index, cTeam ->
                iTeams.filterIndexed { otIndex, _ -> otIndex > index }.forEach {
                    val (scoreA, scoreB) = GenerateScore()
                    m_Matches.add(CMatch(cTeam, it, scoreA, scoreB))
                    if(iHomeAndAway) {
                        val (scoreAAway, scoreBAway) = GenerateScore()
                        m_Matches.add(CMatch(it, cTeam, scoreAAway, scoreBAway))
                    }
                }
            }
        }
        fun CalculatePoints() {
            Matches.forEach {
                when {
                    it.ScoreA > it.ScoreB -> {
                        it.TeamA.Score += m_TableOfScores["WON"] ?: 0
                        it.TeamB.Score += m_TableOfScores["LOST"] ?: 0
                        it.TeamA.Won++
                        it.TeamB.Lost++
                    }
                    it.ScoreB > it.ScoreA -> {
                        it.TeamB.Score += m_TableOfScores["WON"] ?: 0
                        it.TeamA.Score += m_TableOfScores["LOST"] ?: 0
                        it.TeamA.Lost++
                        it.TeamB.Won++
                    }
                    else -> {
                        it.TeamA.Score += m_TableOfScores["DRAWN"] ?: 0
                        it.TeamB.Score += m_TableOfScores["DRAWN"] ?: 0
                        it.TeamA.Draw++
                        it.TeamB.Draw++
                    }
                }
                it.TeamA.GoalsDone += it.ScoreA
                it.TeamA.GoalsTaken += it.ScoreB
                it.TeamB.GoalsDone += it.ScoreB
                it.TeamB.GoalsTaken += it.ScoreA
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
        val Matches
            get() = m_Matches
    }
    //  *** PROPERTIES ***
    //  *** MEMBERS ***
    private var m_ID : Int = 0
    private var m_Game : CGame? = null
    private val m_FileName = Options.GameFile
    //  *** METHODS ***
    fun NewGame(iNoOfTeams : Int) {
        val id = ++ m_ID

        //  Generate teams
        val teams : MutableList<CTeam> = mutableListOf()
        ('A'..'Z').toList().map { it.toString() }.take(iNoOfTeams).forEach {
            teams.add(CTeam(it))
        }

        //  Generate matches
        val calendar = CCalendar(teams, Options.HomeAndAway)

        //  Calculate points for each team
        calendar.CalculatePoints()

        m_Game = CGame(id, teams, calendar.Matches)

        println("Generated new game with $iNoOfTeams Teams")
        ShowQuestion()
    }
    fun CurrentGame() {
        if(m_Game == null)
            println("No current game")
        else
            println("ID: ${m_Game?.ID}, Number of teams: ${m_Game?.Teams?.size}")
    }
    fun ShowQuestion() = m_Game?.Teams?.sortedWith(compareBy { it.Score})?.reversed()?.forEach {
        val wdl = if(Options.ShowWDL) ", WDL (${it.Won}, ${it.Draw}, ${it.Lost})" else ""
        println("${it.Name} - ${it.Score} points, Goals (${it.GoalsDone}, ${it.GoalsTaken})$wdl")
    } ?: println("No game created.")
    fun ShowSolution() = m_Game?.Matches?.forEachIndexed {
        id, match ->
        println("Match ${id + 1} : ${match.TeamA.Name} - ${match.TeamB.Name} = ${match.ScoreA} - ${match.ScoreB}")
    } ?: println("No game created.")
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
    fun LoadDatas() {
        val fm = CFileManager(m_FileName)
        val game = fm.LoadDatas()
        try {
            m_Game = game!!
        }
        catch(e : Exception) {
            println("Can't load file, check if there's any")
        }
    }
    fun SaveDatas() {
        val fm = CFileManager(m_FileName)
        try {
            fm.SaveDatas(m_Game!!)
        }
        catch(e : IllegalStateException) {
            println("Generate a new game first!")
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
) : Serializable
//----------------------------------------------------------------------------------------------------------------------
data class CMatch(
        val TeamA : CTeam,
        val TeamB : CTeam,
        val ScoreA : Int,
        val ScoreB : Int
) : Serializable
//----------------------------------------------------------------------------------------------------------------------
data class CGame(
        val ID : Int,
        val Teams : MutableList<CTeam>,
        val Matches : MutableList<CMatch>
) : Serializable
//----------------------------------------------------------------------------------------------------------------------