package Defs
/*import javax.xml.bind.annotation.*

@XmlRootElement(name = "game")
data class CGame(@XmlElement val ID : Int) {
    constructor() : this(1)

    @XmlElement(name = "teams")
    val teams : MutableCollection<CTeam> = mutableListOf()
}
//----------------------------------------------------------------------------------------------------------------------
@XmlRootElement(name = "team")
@XmlAccessorType(XmlAccessType.FIELD)
data class CTeam(
        @XmlElement val Name : String,
        @XmlElement var Score : Int = 0,
        @XmlElement var GoalsDone : Int = 0,
        @XmlElement var GoalsTaken : Int = 0,
        @XmlElement var Won : Int = 0,
        @XmlElement var Draw : Int = 0,
        @XmlElement var Lost : Int = 0
) {
    //constructor() : this("", 0, 0, 0, 0, 0, Lost = 0)
}
//----------------------------------------------------------------------------------------------------------------------
@XmlRootElement(name = "teams")
data class CTeams {
    @XmlElement(name = "team")
    val teams : MutableCollection<CTeam> = mutableListOf()
}
//----------------------------------------------------------------------------------------------------------------------


@XmlRootElement(name = "room")
@XmlAccessorType(XmlAccessType.FIELD)
data class CRoom(@XmlAttribute override val id : Int, @XmlElement override val description : String, @XmlElement val exit : String) : IBaseObject {
    constructor() : this(0, "No description", "0,0,0,0,0,0")
}
//----------------------------------------------------------------------------------------------------------------------
@XmlRootElement(name = "rooms")
data class CRooms(@XmlAttribute val mapName: String) {
    constructor() : this("Unnamed map")

    @XmlElement(name = "room")
    val rooms : MutableCollection<CRoom> = mutableListOf()
}
*/