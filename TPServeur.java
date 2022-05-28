import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Tom Marchand
 *
 */
public class TPServeur {

	public static boolean[][] grille = new boolean[10][10];
	public static ArrayList<TPJoueur> joueurs = new ArrayList<TPJoueur>();

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// initialisation du serveur
		System.out.println("Serveur démarré");
		initGrille();

		ServerSocket serv = null;
		// création du serveur sur le port 6000
		try {
			serv = new ServerSocket(6000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// attente de nouveaux joueurs en permanence
		while (true) {
			try {
				Socket socket = serv.accept();
				Thread thr = new Thread(new ServeurThreadClient(socket));
				thr.start();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	// construction de la grille
	private static void initGrille() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				TPServeur.grille[i][j] = false;
			}
		}

	}

}

/**
 * utilisation de threads pour gérer chaque joueur
 * cela permet une gestion séparée de chaque joueur sans générer de conflits
 */
class ServeurThreadClient implements Runnable {

	private Socket client;
	private ObjectOutputStream objOutput;
	private ObjectInputStream objInput;
	private TPJoueur joueur;

	/**
	 * 
	 * @param client
	 */
	public ServeurThreadClient(Socket client) {
		this.client = client;
		// récupère les actions des clients
		try {
			this.objOutput = new ObjectOutputStream(this.client.getOutputStream());
			this.objInput = new ObjectInputStream(this.client.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		boolean end = false;
		TPJoueur j = null;

		try {
			j = (TPJoueur) this.objInput.readObject();
			this.joueur = joueurOkInit(j);
			this.objOutput.writeObject(this.joueur);
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		if (this.joueur.getId() == -1) {
			end = true;
		} else {
			TPServeur.joueurs.add(this.joueur);
		}

		while (!end) {
			try {
				// vérification de l'état des joueurs
				byte keep_x = this.joueur.getPosX();
				byte keep_y = this.joueur.getPosY();

				this.joueur.update((TPJoueur) this.objInput.readUnshared());
				joueurOk(this.joueur, keep_x, keep_y);

				etatVerticle(this.joueur);
				etatHorizontal(this.joueur);

				this.objOutput.writeInt(TPServeur.joueurs.size());
				for (int i = 0; i < TPServeur.joueurs.size(); i++) {
					this.objOutput.writeUnshared(TPServeur.joueurs.get(i));
				}

			} catch (IOException e) {
				// si un joueur ferme sa page
				System.out.println("Le client " + this.joueur.toString() + " à quitter");
				TPServeur.joueurs.remove(this.joueur);
				TPServeur.grille[this.joueur.getPosX()][this.joueur.getPosY()] = false;
				end = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * vérification de l'état de blocage horizontal d'un joueur
	 * 
	 * @param j
	 */
	private void etatHorizontal(TPJoueur j) {
		
		if (j.getPosX() != 0 && j.getPosX() != 9) {

			if (TPServeur.grille[j.getPosX() - 1][j.getPosY()] && TPServeur.grille[j.getPosX() + 1][j.getPosY()]) {

				TPJoueur advDroite = advPos((byte) (j.getPosX() + 1), j.getPosY());
				TPJoueur advGauche = advPos((byte) (j.getPosX() - 1), j.getPosY());

				if ((advDroite.getTeam() == advGauche.getTeam()) && advDroite.getTeam() != j.getTeam()) {
					j.setAlive(false);
					j.setTeam(TPTeam.noir);
				}
			}
		}
	}

	/**
	 * vérification de l'état de blocage vertical d'un joueur
	 * @param j
	 */
	private void etatVerticle(TPJoueur j) {
		
		if (j.getPosY() != 0 && j.getPosY() != 9) {

			if (TPServeur.grille[j.getPosX()][j.getPosY() + 1] && TPServeur.grille[j.getPosX()][j.getPosY() - 1]) {

				TPJoueur advSup = advPos(j.getPosX(), (byte) (j.getPosY() + 1));
				TPJoueur advInf = advPos(j.getPosX(), (byte) (j.getPosY() - 1));

				// passage en équipe noir si le joueur est bloqué
				if ((advSup.getTeam() == advInf.getTeam()) && advSup.getTeam() != j.getTeam()) {
					j.setAlive(false);
					j.setTeam(TPTeam.noir);
				}
			}
		}
	}

	/**
	 * retourne les positions des joueurs adverses a chaque update de déplacement
	 * 
	 * @param posX
	 * @param posY
	 * @return
	 */
	private TPJoueur advPos(byte posX, byte posY) {
		for (int i = 0; i < TPServeur.joueurs.size(); i++) {
			if (TPServeur.joueurs.get(i).getPosX() == posX && TPServeur.joueurs.get(i).getPosY() == posY) {
				return TPServeur.joueurs.get(i);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param joueur
	 * @param x
	 * @param y
	 */
	private void joueurOk(TPJoueur joueur, byte x, byte y) {
		if (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX(x);
			joueur.setPosY(y);
		} else {
			TPServeur.grille[x][y] = false;
			TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		}
	}

	/**
	 * apparition d'un joueur
	 * 
	 * @param joueur
	 * @return
	 */
	private TPJoueur joueurOkInit(TPJoueur joueur) {
		for (TPJoueur j : TPServeur.joueurs) {
			if (j.getId() == joueur.getId()) {
				return new TPJoueur((byte) -1, (byte) -1, (byte) -1, joueur.getTeam());
			}
		}
		
		while ( joueur.getPosX() > 9 || joueur.getPosX() < 0 || joueur.getPosY() > 9 || joueur.getPosY() < 0 || TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX((int) (Math.random() * 10));
			joueur.setPosY((int) (Math.random() * 10));
		}
		TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		return new TPJoueur(joueur.getId(), joueur.getPosX(), joueur.getPosY(), joueur.getTeam());

	}

}