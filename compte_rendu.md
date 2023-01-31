
## Note commandes docker
1- Create a dockerfile in the directory
2- build the docker with the following cmd : `docker build -t MyName/appName .`
3- run the docker container `docker run -p 8888:5000 --name appName MyName/appName`


## TP 1
**Build de l'image docker :**
 `docker build -t martin/postgresql .`
**-t** défini le nom de l'image ça lui donne un tag
 
**Lancement de la db :**
 `docker run --net=app-network -e POSTGRES_PASSWORD=pwd -p 5000:5432 -d --name postgresM martin/postgresql`

**--net=app-network** permet de lui dire d'utiliser le réseau créé au préalable identifié par le nom : `app-network`

**-e** permet d'indiquer qu'on rajoute une variable d'environnement dans notre cas il s'agit de `POSTGRES_PASSWORD` pour éviter de le stocker dans un fichier

**-p** expose : permet la redirection de ports

**-d** créer le container dans un processus détaché de notre terminal 


 **Création du réseau**
 `docker network create app-network`
 Lancement de adminer : `docker run -p 8090:8080 --net=app-network --name=adminer -d adminer`
 
Dockerfile avec l'intialisation de la base avec les scripts sql :

> FROM postgres:14.1-alpine
> 
> ENV POSTGRES_DB=db` ENV POSTGRES_USER=usr
> 
> COPY /data /docker-entrypoint-initdb.d

**Lancement de l'appli PostgresSQL**
`docker run --net=app-network -e POSTGRES_PASSWORD=pwd -p 5000:5432 -v /data/:/var/lib/postgresql/data -d --name postgresM martin/postgresql`
Ici on lui dit d'utiliser notre réseau, on lui rajoute des variables d'environnements. Contrairement aux variables dans le build, celles-ci sont injectés au runtime de l'appli, on peut donc sécuriser notre mot de passe de base de données.
On vient aussi spécifier une redirection de port avec le *-p* pour dire que notre port 5000 renvoi vers le port 5432 de notre containeur. On lance le containeur en détaché avec le *-d*.

**Lancement du serveur spring :**
`docker run --net=app-network -p 8000:8080 --name SimpleApi martin/simpleapi`
Une redirection de port à été rajouté mais reste optionnel.
Dans le application.yml de notre projet spring on vient changer l'adresse de la database pour lui indiquer d'aller chercher le conteneur qui se situe sur le même 

**Lancement du serveur apache :**
`docker run --net=app-network -d -p 80:80 --name apache-server martin/apache-server`

>FROM  httpd:2.4
>
>#Copie des fichier dans le serveur
>COPY  .  /usr/local/apache2/htdocs/
>
>#Ecrase la conf pour rajouter le reverse proxy
>COPY  httpd.conf  /usr/local/apache2/conf/httpd.conf

Dans notre cas un deuxième réseau aurais pu être rajouté pour laisser communiquer uniquement notre serveur API et le front en isolant notre base de données a des fin de sécurisation.

Création d'un reverse-proxy, il permet de redistribuer les requêtes vers les serveurs que l'on souhaite, il y a aussi une possibilité de faire du load balacing (redirection harmonieuse des requêtes pour équilibrer les resources utilisés sur les serveurs). 
Dans notre cas il redirige simplement vers notre serveur API.

**Création d'un docker-compose**
Le docker compose permet de regrouper plusieurs conteneurs. Ici on vient déclarer nos 3 conteneurs, backend, database et httpd. Le dockercompose va venir récupérer le fichier Dockerfile de chaque dossier spécifier pour construire chaque image.
