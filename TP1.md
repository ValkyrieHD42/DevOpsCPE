
## Note commandes docker
1- Create a dockerfile in the directory
2- build the docker with the following cmd : `docker build -t MyName/appName .`
3- run the docker container `docker run -p 8888:5000 --name appName MyName/appName`


## TP 1
**Build de l'image docker :**
 `docker build -t martin/postgresql .`
**-t** défini le nom de l'image ça lui donne un tag
 
**Lancement de la db :**
 `docker run --net=app-network -e POSTGRES_PASSWORD=pwd -p 5000:5432 -d -v /data/:/var/lib/postgresql/data --name postgresM martin/postgresql`

**--net=app-network** permet de lui dire d'utiliser le réseau créé au préalable identifié par le nom : `app-network`

**-e** permet d'indiquer qu'on rajoute une variable d'environnement dans notre cas il s'agit de `POSTGRES_PASSWORD` pour éviter de le stocker dans un fichier

**-p** expose : permet la redirection de ports

**-d** créer le container dans un processus détaché de notre terminal 

**-v** permet le montage d'un volume : on vient monter un dossier pour permettre la peristance des données en base

**Dockerfile du Postgres**
> FROM postgres:14.1-alpine
> 
> ENV POSTGRES_DB=db` ENV POSTGRES_USER=usr
> 
> COPY /data /docker-entrypoint-initdb.d

On lui indique de prendre postgres avec une version 14.1-alpine
On lui donne des variables d'environnements, nécessaires au lancement de Postgres
Et enfin on colle nos script dans un le `docker-entrypoint-initdb.d`, cela permet d'éxécuter les scripts au démarage de l'application.

**Lancement de l'appli PostgresSQL**
`docker run --net=app-network -e POSTGRES_PASSWORD=pwd -p 5000:5432 -v /data/:/var/lib/postgresql/data -d --name postgresM martin/postgresql`
Ici on lui dit d'utiliser notre réseau, on lui rajoute des variables d'environnements. Contrairement aux variables dans le build, celles-ci sont injectés au runtime de l'appli, on peut donc sécuriser notre mot de passe de base de données.
On vient aussi spécifier une redirection de port avec le *-p* pour dire que notre port 5000 renvoi vers le port 5432 de notre containeur. On lance le containeur en détaché avec le *-d*.

![connexion réussi](https://user-images.githubusercontent.com/57757196/215780836-6c2e7a1e-d77c-42b9-b27f-4a3261f9bd28.png)

 **Création du réseau**
 `docker network create app-network`
 Lancement de adminer : `docker run -p 8090:8080 --net=app-network --name=adminer -d adminer`
 
 ![create network](https://user-images.githubusercontent.com/57757196/215780922-2d1bd423-1b6e-49f1-a200-7a8d2f5d7ff9.png)
 
Dockerfile avec l'intialisation de la base avec les scripts sql :

**Condiguration du serveur spring**
Attention, avant de lancer le serveur on doit configurer l'accès à la base de données. Pour cela le fichier application.yml regroupes les infos de l'appli, on y retrouve une section datasource, ce champs doit être modifier pour indiquer l'adresse de la base de données du réseau virtuel que l'on vient de créer.

![applicationymlconfig](https://user-images.githubusercontent.com/57757196/215781564-b2ac1eae-199e-4467-8d71-7cb3743b0ac2.png)

Docker permet de relier les éléments du réseau en associant une adresse IP à leur nom de containeur.

Dockerfile :
> #Build  
> FROM maven:3.8.6-amazoncorretto-17 AS myapp-build
> ENV MYAPP_HOME /opt/myapp
> WORKDIR $MYAPP_HOME
> COPY pom.xml .
> COPY src ./src
> RUN mvn package -DskipTests
>
> #Run  
> FROM amazoncorretto:17
> ENV MYAPP_HOME /opt/myapp
> WORKDIR $MYAPP_HOME
> COPY --from=myapp-build $MYAPP_HOME/target/*.jar $MYAPP_HOME/myapp.jar
>
> ENTRYPOINT java -jar myapp.jar

Ici on décompose le build en plusieurs, "Multistage build" cela permet récupérer uniquement ce qui nous ai nécessaire pour lancer l'application.

**Lancement du serveur spring :**
`docker run --net=app-network -p 8000:8080 --name SimpleApi martin/simpleapi`
Une redirection de port à été rajouté mais reste optionnel. Notre serveur se lance :

![lancement du spring](https://user-images.githubusercontent.com/57757196/215782794-3cc1863c-4618-4ab5-8732-a050502d0257.png)

On peut déjà tester nos données en effectuant des requêtes, dans le naviguateur ou via un outil de requêtage d'API comme Postman.

![working app](https://user-images.githubusercontent.com/57757196/215783022-07a54b37-9eb5-45d2-963e-cba37fdd9977.png)
On peut voir que nos données sont bien récupérables et que l'API est accessible.

**Configuration du serveur apache**

> FROM  httpd:2.4
>
> #Copie des fichier dans le serveur  
> COPY  .  /usr/local/apache2/htdocs/
>
> #Ecrase la conf pour rajouter le reverse proxy  
> COPY  httpd.conf  /usr/local/apache2/conf/httpd.conf

Dans notre cas un deuxième réseau aurais pu être rajouté pour laisser communiquer uniquement notre serveur API et le front en isolant notre base de données a des fin de sécurisation.

**Lancement du serveur apache :**
`docker run --net=app-network -d -p 80:80 --name apache-server martin/apache-server`

![apache server working](https://user-images.githubusercontent.com/57757196/215784147-4b922863-6f61-4b47-b11c-c082b1d29b73.png)

Le fichier httpd.conf à été modifier pour activer les modules de ReversePorxy/ProxyHTTP et les lignes suivantes ont été ajoutés à la configuration :

![conf virtualhost](https://user-images.githubusercontent.com/57757196/215783689-4dd024e7-5978-4148-9164-6094d164a61f.png)

Création d'un reverse-proxy, il permet de redistribuer les requêtes vers les serveurs que l'on souhaite, il y a aussi une possibilité de faire du load balacing (redirection harmonieuse des requêtes pour équilibrer les resources utilisés sur les serveurs). 
Dans notre cas il redirige simplement vers notre serveur API.

**Création d'un docker-compose**
Le docker compose permet de regrouper plusieurs conteneurs. Ici on vient déclarer nos 3 conteneurs, backend, database et httpd. Le dockercompose va venir récupérer le fichier Dockerfile de chaque dossier spécifier pour construire chaque image.

![image](https://user-images.githubusercontent.com/57757196/215785211-c4fbf1a8-93cc-457a-9850-c46b0824f6b4.png)

Dans cette configuration, on reprends la plus part des arguments passés en paramètre de la commande pour les formaliser dans la configuration du docker-compose. Il y a quand même une nouvelle subtilité avec le **depends_on** qui permet de dire quel conteneur à besoin de se lancer en premier ou avant un autre.

Pour lancer le docker compose on éxecute la commande suivante : **docker-compose up**
on peut arrêter un docker-compose avec **docker-compose stop** ou simple avec un **CTRL+C** quand on se trouve dans un terminal

On peut donc ouvrir un navigateur et intéroger la page *http://localhost:8080/students*, celui ci est censé nous rediriger vers le backend grâce à l'implémentation du reverse proxy.

![image](https://user-images.githubusercontent.com/57757196/215786456-28d2d427-5c62-4b39-ba37-56b88adb9dae.png)

