#!/bin/bash
# 🚀 Script de build et déploiement automatique pour un microservice Kubernetes sous Minikube

# Vérifie la présence des arguments (nom du service et tag)
if [ $# -ne 2 ]; then
  echo "❌ Usage: ./update-service.sh <service-name> <tag>"
  exit 1
fi

SERVICE=$1
TAG=$2

echo "🔧 Service: $SERVICE"
echo "🏷️ Tag: $TAG"

# Aller dans le dossier du microservice
cd $SERVICE || { echo "❌ Dossier $SERVICE introuvable"; exit 1; }

# Nettoyer et recompiler sans les tests
echo "🧹 Nettoyage et compilation Maven..."
mvn -q -DskipTests clean package || { echo "❌ Erreur Maven"; exit 1; }

# Construire l'image Docker directement dans Minikube
echo "🐳 Construction de l'image Docker..."
docker build -t $SERVICE:$TAG . || { echo "❌ Échec du build Docker"; exit 1; }

# Mettre à jour le déploiement Kubernetes
echo "⚙️ Configuration de l'environnement Docker de Minikube..."
eval $(minikube docker-env)
# Active le Docker interne de Minikube (les images seront visibles par le cluster)

echo "🚀 Déploiement sur le cluster..."
minikube image load $SERVICE:$TAG

echo "🚀 Mettre à jour le déploiement dans Kubernetes..."
kubectl -n challengr set image deploy/$SERVICE app=$SERVICE:$TAG

# Attendre la mise à jour complète
kubectl -n challengr rollout status deploy/$SERVICE

# Afficher les pods pour vérifier le nouveau conteneur
kubectl get pods -n challengr

# 6️⃣ Vérifier les logs
kubectl -n challengr logs deploy/$SERVICE --tail=20

