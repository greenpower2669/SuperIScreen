# SuperIScreen 🎬

Application Android native (Java), version **0.1.0**, pour transformer une vidéo personnelle en fond d'écran animé.

## Pour Yann : solution immédiate sur Samsung Galaxy
La vidéo MP4 partagée (2,3 s, verticale) peut être utilisée **sans APK** :
1. Ouvrir la vidéo dans **Galerie**.
2. Menu **⋮** → **Définir comme fond d'écran** → **Écran de verrouillage**.
3. Vérifier le cadrage et choisir **Terminé**.

Autre parcours Samsung : appui long sur l'écran verrouillé → Fonds d'écran → Galerie → vidéo → Terminé.

Source : [assistance Samsung](https://www.samsung.com/us/support/answer/ANS10001632/).

## Application Android
L'application permet de :
- choisir un fichier vidéo depuis le sélecteur Android ;
- en conserver une copie dans le stockage privé de l'application ;
- ouvrir l'aperçu système des fonds d'écran animés ;
- lire la vidéo **en boucle et sans son**, en arrêtant la lecture quand le fond d'écran n'est plus visible.

**Limite Android :** le choix « écran d'accueil », « écran verrouillé » ou « les deux » dépend du fabricant. L'application ne peut pas forcer un fond animé sur le verrouillage seul. Pour cet usage précis, la fonction Galerie de Samsung est recommandée.

## Construction
JDK 17, SDK Android 36, Gradle 8.13, plugin Android Gradle 8.10.1.

```sh
gradle :app:assembleDebug
```

La CI GitHub Actions compile l'APK debug après chaque push sur la branche de travail. Une balise `v*` pourra publier l'APK en GitHub Release **après tests et validation**. L'AAB est réservé à une éventuelle publication Play Store.

L'APK debug, lorsqu'il existe, est destiné aux tests et signé avec une clé de debug ; il ne doit pas être présenté comme une version de production.

## Respect des médias
La vidéo envoyée dans la conversation n'est **pas publiée dans ce dépôt public**. SuperIScreen l'importe localement sur le téléphone, sans envoyer le contenu sur un serveur.

## Suivi du projet
Voir `brain.md`, `brainmap.md`, `debughistorical.md`, `todo.md` et `ordres-de-mission.md`. Aucun merge sur `main` sans validation de Fab.
