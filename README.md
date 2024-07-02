# Data-Engineering-Project
## Explication de l'architecture
Notre startup collecte des données en temps réel à partir de divers périphériques IoT (a l'aide de moustique piquant les individus dans la rue et renvoyant leur donnees personelles). Ces données sont ensuite acheminées vers un flux Kafka pour leur traitement. Ce flux distribue les données vers deux consommateurs distincts.

Le premier consommateur est chargé de stocker les données pour une analyse à long terme. Les données brutes sont injectées dans un data lake pour être ensuite traitées à intervalles fixes, par exemple chaque matin en raison du faible volume de données à ce moment-là. Ce traitement se fait à l'aide d'un batch processing, avec SPARK. Une fois traitées, ces données sont stockées dans une base de données CP (Consistency and Partition Tolerance). Notre application d'analyse interroge la base de données afin de générer les analyses nécessaires.

Le deuxième consommateur collecte les données pour notre service d'alertes. Ces données sont traitées en temps réel à l'aide d'un stream processing. Les alertes générées sont envoyées par un bot discord. 
