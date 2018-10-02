## Guide d'utilisation pour la TP1 : projet collaboratif

Ce projet comporte 2 fichiers principaux :
* Client.java
* Server.java

### Client.java
C'est le client qui va se connecter au serveur et qui va envoyer les requêtes.
Une requête est un message. Dans la requête on va avoir le nom de la classe demandée, sa méthode à invoquer ainsi que ses paramètres.
Le format d'un message est : Calc&add&3,5

Ensuite, le client choisit le mode de traitement de ce message :
* SourceColl : on envoi la source du fichier => correspond à 0
* ByteColl : on envoi le byte code d'un fichier => correspond à 1
* ObjectColl : on envoi un objet => correspond à 2

### Server.java
C'est le serveur. Le serveur va recevoir des messages envoyés du client. Le serveur extrait les informations et traite le message en fonction du mode de traitement.

### Comment ça marche ?
On lance le serveur, puis on lance le client. 
Dans le premier paramètre du Client, on envoi soit 0, 1 ou 2 pour choisir le mode de traitement.
On voit à la fin que le serveur et le client communiquent bien.
Exemple de lancement dans Client.java : 
```java
Client client = new Client();
client.execute(2, "Calc&add&3,5"); **on appelle le mode 2**
```

### Traces d'exécution de l'application
En mode 0 => envoi la source, puis le serveur compile, load et exécute la méthode de la classe demandée : 
* Ouput coté client :
```
end the size : 299
The result is : 8
```

* Output côté serveur :
```
Waiting for connections...
Connexion Ok
Protocole ok
Message ok 12
Byte size received => 299
Here
We create the file
Reponse 0
business.Calc
Result : 8
Read everything
```

En mode 2 => envoi d'un objet sérializé, le serveur desérialize et exécute la méthode de la classe demandée :
* Output côté client : 
```
The result is : 8
```

* Output côté serveur : 
```
Waiting for connections...
Connexion Ok
Protocole ok
Message ok 12
Calc
Read everything
Result : 8
```
