import MenuManager.*

fun main(args : Array<String>) {
    val mm = MenuManager("SIMO SOCCER GAME")
    val game = CGameManager()
    val mnu_1 = CMenu("1", "4 teams game", { (game::NewGame)(4) })
    val mnu_2 = CMenu("2", "6 teams game", { (game::NewGame)(6) })
    val mnu_5 = CMenu("l", "Show current games", game::CurrentGame)
    val mnu_6 = CMenu("r", "Show question", game::ShowQuestion)
    val mnu_7 = CMenu("s", "Show solution", game::ShowSolution)
    val mnu_Settings = CMenu("set", "Settings", {})
    val mnu_ShowWDL = CMenuVar<Boolean>("s", "Show Won-Draw-Lost", {
        Options.ShowWDL = ! Options.ShowWDL
    }, {
        return@CMenuVar Options.ShowWDL
    }, mnu_Settings)
    val mnu_HomeAndAway = CMenuVar<Boolean>("h", "Home and away matches", {
        Options.HomeAndAway = ! Options.HomeAndAway
    }, {
        return@CMenuVar Options.HomeAndAway
    }, mnu_Settings)

    mm.run {
        AddMenu(listOf(mnu_1, mnu_2, mnu_5, mnu_6, mnu_7, mnu_Settings, mnu_ShowWDL, mnu_HomeAndAway))
        Interpreter()
    }
}
