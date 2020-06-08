# ProjResIot
Projet Chenillard KNX


# Objectif de notre application

L'objectif était de réaliser un chenillard sur une maquette KNX, puis développer un interface web pour contrôler ce chenillard.
L’interface doit comme la maquette, être composée de plusieurs boutons permettant de modifier les paramètres du chenillard comme sa vitesse, son état ou bien sa direction.
Ce projet à été fait dans le cadre d'un cours en IoT , cela nous permet de réaliser un projet concret dans les communications sans fil.

# Utilisation et organisation 

Notre application se subdivise en deux dossiers situé dans src : 

  Le dossier Chenillard qui comporte : 

    .La classe chenillard permettant de créer un chenillard facilement modifiable.
    En effet, son constructeur prend en paramètres la vitesse, sa direction et son nombre de lampes.
    On y retrouve les fonctions run() et stoprunning() .
 

Le dossier Serveur qui comporte : 
		
    .La classe App qui s’occupe de la création du serveur et de l’utilisation des servlets.

    .La classe EntryPoint qui permet d’effectuer des méthodes GET et de gérer toute la partie HTML

     .Le fichier pom.xml permet de gérer les dépendances également.
     
     .Un fichier HTML dans le main/ qui est appelé par la classe EntryPoint afin de modifier le rendu de la page.
     
 Afin de lance notre projet il suffit de lancer notre serveur/main App.java et se rendre à l'adresse http://localhost:8080/entry-point/home
Lors du lancement la classe App va lancer EntryPoint qui va ouvrir la page HTML ainsi qu'initilaiser le chenillard. D'ici tous sera modifier et les boutons sur le chenillard comme sur la page web sont interconnectés.

![alt text](https://github.com/EvanRenard/ProjResIot/blob/master/image.JPG?raw=true)
			




