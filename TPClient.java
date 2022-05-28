import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Tom Marchand
 * 
 */
public class TPClient extends Frame {

	int port = 6000;
	Socket socket = null;

	static TPJoueur joueur;
	TPPanel tpPanel;
	TPCanvas tpCanvas;
	Timer timer;
	static final int LONGUEUR = 650; //
	static final int HAUTEUR = 710;

	/** Constructeur */
	// number dans le fichier initial est devenu id
	public TPClient(byte id, byte team, byte x, byte y) {
		setLayout(new BorderLayout());
		tpPanel = new TPPanel(this);
		add("North", tpPanel);
		tpCanvas = new TPCanvas();
		add("Center", tpCanvas);
		// - - - -
		TPClient.joueur = new TPJoueur(id, x, y, TPTeam.getTeamById(team));
		try {
			this.socket = new Socket("localhost", this.port);
			Thread threadClient = new Thread(new ThreadClient(socket, tpCanvas));
			threadClient.start();
		} catch (IOException e) {
			System.err.println("Pas de Serveur: " + e.getLocalizedMessage());
			System.exit(-1);

		}

	}

	/** Action vers droit */
	public synchronized void droit() {
		if (TPClient.joueur.isAlive()) {
			if ((TPClient.joueur.getPosX() + 1) < 10) {
				TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers gauche */
	public synchronized void gauche() {
		if (TPClient.joueur.isAlive()) {
			if ((TPClient.joueur.getPosX() - 1) >= 0) {
				TPClient.joueur.setPosX(TPClient.joueur.getPosX() - 1);
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers gauche */
	public synchronized void haut() {
		if (TPClient.joueur.isAlive()) {
			if ((TPClient.joueur.getPosY() - 1) >= 0) {
				TPClient.joueur.setPosY(TPClient.joueur.getPosY() - 1);
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers bas */
	public synchronized void bas() {
		if (TPClient.joueur.isAlive()) {
			if ((TPClient.joueur.getPosY() + 1) < 10) {
				TPClient.joueur.setPosY(TPClient.joueur.getPosY() + 1);
				tpCanvas.repaint();
			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Usage : java TPClient identifiant id_equipe posX posY ");
			System.exit(0);
		}
		try {

			byte numero = (byte) Integer.parseInt(args[0]);
			byte team = (byte) Integer.parseInt(args[1]);
			byte posX = (byte) Integer.parseInt(args[2]);
			byte posY = (byte) Integer.parseInt(args[3]);
			posX--;
			posY--;

			TPClient tPClient = new TPClient(numero, team, posX, posY);

			// Pour fermeture
			tPClient.setResizable(false);
			tPClient.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			// Create Panel back forward
			tPClient.pack();
			tPClient.setSize(LONGUEUR, HAUTEUR);
			tPClient.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}

/**
 * thread créé pour chaque joueur
 * 
 * @author Tom Marchand
 *
 */
class ThreadClient implements Runnable {

	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	private TPCanvas canvas;

	/**
	 * 
	 * @param socket
	 * @param canvas
	 */
	public ThreadClient(Socket socket, TPCanvas canvas) {
		this.canvas = canvas;
		try {
			this.objOutput = new ObjectOutputStream(socket.getOutputStream());
			this.objInput = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean fini = false;
		
		// initialisation joueur avec vérifiction d'id
		try {
			this.objOutput.writeUnshared(TPClient.joueur);
			TPClient.joueur = (TPJoueur) objInput.readUnshared();
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		if (TPClient.joueur.getId() == -1) {
			System.err.println("Identifiant déjà utilisé");
			fini = true;
			System.exit(0);
		}
		
		// boucle pour envoi et réception des joueurs
		while (!fini) {
			try {
				Thread.sleep(120);
				
				// ajout du joueur dans le serveur
				this.objOutput.writeUnshared(TPClient.joueur);
				
				// liste des joueurs
				canvas.joueurs = new ArrayList<TPJoueur>();
				int size = objInput.readInt();
				for (int i = 0; i < size; i++) {
					TPJoueur j = (TPJoueur) objInput.readUnshared();
					
					// actualistion des coordonnées du joueur
					TPClient.joueur.update(j);
					canvas.joueurs.add(j);
				}
				canvas.repaint();

			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
