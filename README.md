# TEA Réseau et Transmission

## Règles
**Nombre de joueurs et fonctionnement**

- minimum 4 joueurs (répartis en 2 équipes)
- maximum 4 équipes
- le jeu se déroule en temps réel et non en tour par tour

**But du jeu**

- Le but est d'éliminé tout les joueurs de l'équipe adverse en les encerclant verticalement ou horizontalement
---
## Installation

***Requiert openjdk-11***
```bash
git clone https://github.com/tommrchd/TeaRetT

cd TeaRetT/

javac *.java
```
---
## Comment jouer ?

- Lancement du serveur :
  ```bash
    java TPServeur
  ```
- Création d'un joueur (à refaire pour chaque ajout de joueur) :
  ```bash
    java TPClient <id_joueur> <id_equipe> <positionX> <positionY>
  ```
<`id_joueur`>
- 2 joueurs ne peuvent pas avoir le même id !

<`id_equipe`>
- 1 = bleu
- 2 = rouge
- 3 = vert
- 4 = jaune