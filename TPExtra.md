# Load Balancer

Pour la création du loadbalancer il a fallu créer deux container backend-1 et backend-2.  
Modification dans le http.conf :  
```
<Proxy "balancer://mycluster">
  BalancerMember "http://backend-1:8080"
  BalancerMember "http://backend-2:8080"
</Proxy>
ProxyPreserveHost On
ProxyPass "/" "balancer://mycluster/"
ProxyPassReverse "/" "balancer://mycluster/"
```
Des modules http on du être activés, à noter que c'est en activant/désactivant des modules qu'on choisi l'algorithme du loadbalancer. Dans notre cas on le laisse par défaut.  

# Front

Pour la mise en place du front, on a du modifier le http.conf : ajout d'un virtual host sur le port 8080 pour l'appel du back avec du loadbalancing et sur le port 80 on renvoi vers notre container front.

```
 <VirtualHost *:80>
	ProxyPreserveHost On
	ProxyPass "/" "http://front:80/"
	ProxyPassReverse "/" "http://front:80/"
</VirtualHost>

<VirtualHost *:8080>
	<Proxy "balancer://mycluster">
		BalancerMember "http://backend-1:8080"
		BalancerMember "http://backend-2:8080"
	</Proxy>
	ProxyPreserveHost On
	ProxyPass "/" "balancer://mycluster/"
	ProxyPassReverse "/" "balancer://mycluster/"
</VirtualHost>
```
