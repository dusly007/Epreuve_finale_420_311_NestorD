# Rush au resto – Épreuve finale 420-311 ✅ **TERMINE**

**Projet Java console simulant un restaurant pendant le rush de midi.**  
**Note : Projet complet et fonctionnel, testé avec les scénarios fournis.**

## 📋 Informations étudiantes

- **Nom** : Dusly Nestor
- **DA** : 2395223
- **Cours** : 420-311 – Structures de données
- **Enseignant(e)** : Sara Boumehraz
- **Date de remise** : 16 décembre 2025

##  Objectif 

L'application simule complètement le service d'un restaurant :  
  -Clients arrivent avec patience limitée  
  -Commandes multi-plats (PIZZA🍕, BURGER🍔, FRITES🍟)  
  -**Thread Cuisinier** concurrent prenant les commandes en file  
  -Temps simulé avec `tick()` (patience ↓, préparation ↓)  
  -Clients servis 😋 ou partis fâchés 😡  
  -**Stats complètes** : CA, clients servis/fâchés, plats vendus  
  -**Sortie 100% conforme** au format demandé (fichier logs)

## Architecture implémentée

mv.sdd/
├── App.java # Point d'entrée 
├── io/ # Lecture actions 
│ ├── ActionFileReader.java
│ ├── ActionParser.java
│ └── ActionType.java
├── model/ # Entités 
│ ├── Client.java
│ ├── Commande.java
│ ├── Stats.java (EnumMap plats)
│ ├── Horloge.java
│ └── ...
├── sim/ # Simulation principale 
│ └── Restaurant.java # tick(), états, synchronisation
└── sim.thread/ # Concurrence 
└── Cuisinier.java # Thread Runnable
└── utils/ # Outils 
├── Logger.java
├── Formatter.java # clientLine() corrigé
└── Constantes.java


## Structures de données utilisées

| Structure | Usage | Pourquoi |
|-----------|--------|----------|
| `HashMap<Integer, Client>` | Clients présents | Recherche O(1) par ID |
| `ConcurrentLinkedQueue<Commande>` | File commandes | Thread-safe, FIFO |
| `synchronizedList<Commande>` | Commandes en prépa | Accès concurrent |
| `EnumMap<MenuPlat, Integer>` | Stats ventes plats | Parfait pour enum |

##  Compilation & Exécution

### 1. Compiler
mvn clean package
→ Génère `target/2395223-Epreuve_finale_420_311.jar`

### 2. Exécuter (comme exigé)
mvn exec:java -Dexec.mainClass="mv.sdd.App"
-Dexec.args="data/scenario_1.txt data/sortie_1.txt"


**OU avec JAR :**
java -jar target/2395223-Epreuve_finale_420_311.jar
data/scenario_1.txt data/sortie_1.txt


## Fichiers livrables inclus

- `data/scenario_simple.txt` (scénario personnel)
-  `data/sortie_simple.txt` (sortie générée)
-  `data/scenario_1.txt` → `data/sortie_1.txt` (scénario enseignant)
-  `target/[TON_DA]-Epreuve_finale_420_311.jar`

## 🔗 Dépôt GitHub
**Repository public** : https://github.com/la-sarita/Epreuve_finale_420_311  
**Invitée** : sara.boumehraz@cegepmv.ca (au cas où)

**Projet prêt pour remise !**
