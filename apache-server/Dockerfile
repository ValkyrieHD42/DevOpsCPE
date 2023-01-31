FROM httpd:2.4
 
# Copie des fichier dans le serveur 
COPY . /usr/local/apache2/htdocs/

# Ecrase la conf pour rajouter le reverse proxy
COPY httpd.conf /usr/local/apache2/conf/httpd.conf