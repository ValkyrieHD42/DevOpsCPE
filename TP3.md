# TP 3 Ansible

## Introduction

Pour ce TP Paul était malade et n'a pas pu être présent il a donc été fait seul.
Ansible va nous permettre d'automatiser des étapes de déploiement, on va pouvoir dans notre cas déployer nos conteneur docker sur un serveur distant à partir de nos images.

Dans un premier temps on vient initaliser un "inventories" 

setup.yml :
```
all:
 vars:
  ansible_user: centos
  ansible_ssh_private_key_file: id_rsa
 children:
  prod:
   hosts: paul.floret.takima.cloud
```

Pour notre tp un serveur martin.jourjon.takima.cloud à été utilisé mais celui-ci à planté aux alentour des 15h bloquant l'avancement du tp. 
Paul à pu me transférer ces accès à la place. Les crash étaient causé par les serveurs back du loadbalacing qui prenaient trop de ram.

**On test l'inventory avec la commande :** `ansible all -i inventories/setup.yml -m ping`  

**Pour regarder l'OS** : `ansible all -i inventories/setup.yml -m setup -a "filter=ansible_distribution*"`  

**Désinstallation d'apache :** `ansible all -i inventories/setup.yml -m yum -a "name=httpd state=absent" --become`  

## Playbook

Les playbook servent à lancer une série d'actions.

Test d'un playbook qui effectue juste un ping
playbook.yml 
```
- hosts: all
  gather_facts: false
  become: yes

  tasks:
  - name: Test Connection
    ping:
```
le *ecome: yes* nous permet de lancer les task avec des privilèges admin
Pour le lancer on utilise la commande : `ansible-playbook -i inventories/setup.yml playbook.yml`

## Role
On va venir découper nos actions en plusieurs roles, chaque rôle peut être appelé dans différents playbook.
Cela nous permet de réutiliser nos actions dans plusieurs playbook et d'éviter les redondances tout en structurant notre déploiement.

Commande pour initialiser un role : `ansible-galaxy init roles/docker`

### Docker installation
**Role name :** Docker  
**Task ->** main.yml
```
---
# tasks file for roles/docker
  - name: Clean packages
    command:
      cmd: dnf clean -y packages

  - name: Install device-mapper-persistent-data
    dnf:
      name: device-mapper-persistent-data
      state: latest

  - name: Install lvm2
    dnf:
      name: lvm2
      state: latest

  - name: add repo docker
    command:
      cmd: sudo dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo

  - name: Install Docker
    dnf:
      name: docker-ce
      state: present

  - name: install python3
    dnf:
      name: python3

  - name: Pip install
    pip:
      name: docker

  - name: Make sure Docker is running
    service: name=docker state=started
    tags: docker
 ```
 Ce rôle nous permet d'installer docker sur le serveur cible
 
 ### Network
**Role name :** network  
**Task ->** main.yml
```
---
# tasks file for roles/network
- name: Create a network
  docker_network:
   name: app-network
```
Ce role nous permet de créer un réseau docker pour nos futurs container

 ### Database
**Role name :** database  
**Task ->** main.yml
```
- name: Create db container and connect to network
  docker_container:
    name: database
    image: valkyriehd/database:latest
    volumes:
      - /data/:/var/lib/postgresql/data
    env:
      POSTGRES_PASSWORD: pwd
    networks:
      - name: app-network
```
Ici on vient créer un container postgresql à partir de notre image database, on lui indique de prendre la dernière. On monte un volume pour la sauvegarde des données et on lui indique étgalement un son mot de passe pour se lancer et son réseau à utiliser.

### Backend
**Role name :** app  
**Task ->** main.yml
```
---
# tasks file for roles/app
- name: Launch backend-1 server
  docker_container:
    name: backend-1
    image: valkyriehd/simple-api:latest
    networks:
      - name: app-network
```
on lui indique quel réseau utilisr.  
Note : Il porte le numéro 1 car plus tard un loadbalancer sera en place, chaque containeur back aura son numéro.
je l'ai volontairement retirer car 2 serveurs back faisait planter la machine, mais un rajout d'un serveur est envisageable.

### Front
**Role name :** proxy  
**Task ->** main.yml
```
# tasks file for roles/proxy
- name: Launch httpd server
  docker_container:
    name: httpd
    image: valkyriehd/httpd:latest
    networks:
      - name: app-network
    ports:
    - 80:80
    - 8080:8080
```
On lui indique la liste des ports à exploser

### Playbook
**Name:** launcher-app.yml

```
- hosts: all
  gather_facts: false
  become: yes
  roles: 
    - network
    - database
    - app
    - proxy
```

**Note :** la propriété *depends_on* n'existe pas avec ansible, cependant cela est reproduisible car nous allons indiquer nous même quel role appelé dans quel ordre dans le playbook.
