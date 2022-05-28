import java.io.Serializable;

/**
 * @author Tom Marchand
 *
 */
public class TPJoueur implements Serializable {

	private byte id;
	private byte posX;
	private byte posY;
	private TPTeam team;
	private boolean alive;

	/**
	 * construction d'un joueur
	 * 
	 * @param id
	 * @param posX
	 * @param posY
	 * @param team
	 */
	public TPJoueur(byte id, byte posX, byte posY, TPTeam team) {
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		this.alive = true;
	}

	/**
	 * 
	 * @return
	 */
	public byte getId() {
		return id;
	}
	
	/**
	 * 
	 * @return
	 */
	public byte getPosX() {
		return posX;
	}

	/**
	 * 
	 * @param i
	 */
	public void setPosX(int i) {
		this.posX = (byte) i;
	}

	/**
	 * 
	 * @return
	 */
	public byte getPosY() {
		return posY;
	}

	/**
	 * 
	 * @param i
	 */
	public void setPosY(int i) {
		this.posY = (byte) i;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * 
	 * @param alive
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * 
	 * @return
	 */
	public TPTeam getTeam() {
		return team;
	}

	/**
	 * 
	 * @param team
	 */
	public void setTeam(TPTeam team) {
		this.team = team;
	}

	/**
	 * fonction de mise à jour d'un joueur pour chaque déplacement de ce joueur ou d'un autre
	 * 
	 * @param joueur
	 */
	public void update(TPJoueur joueur) {
		if (this.id == joueur.id) {
			this.posX = joueur.posX;
			this.posY = joueur.posY;
			this.team = joueur.team;
			this.alive = joueur.alive;
		} else {
			System.err.println("Le joueur n°" + this.id + " ne peut pas etre mis a jour.");
		}
	}
	
	public String toString() {
		return "Joueur n°" + this.id + ", equipe " + this.team + " (" + this.posX + "," + this.posY + ")";
	}
}
