import java.awt.Color;

/**
 * @author Tom Marchand
 *
 */
public enum TPTeam {

	/**
	 * 5 couleurs d'équipe en fonction de l'id
	 * 0 (noir) réservé pour un joueur bloqué
	 */
	noir(0, Color.black),
	bleu(1, Color.blue),
	rouge(2, Color.red),
	vert(3, Color.green),
	jaune(4, Color.yellow);
	
	private int id;
	private Color couleur;
	
	TPTeam(int id, Color color){
		this.id = id;
		this.couleur = color;
	}

	public Color getCouleur() {
		return couleur;
	}
	
	public static TPTeam getTeamById(int id) {
		TPTeam teamResult = null;
		for (TPTeam team : TPTeam.values()) {
			if(team.id == id) {
				teamResult = team;
			}
		}
		return teamResult;
	}
}