#!/bin/bash
# === 🔄 Script de mise à jour automatique de la webapp Challengr ===
# Usage : ./update-webapp.sh [TAG_OPTIONNEL]
# Si aucun tag n’est donné, on utilise "latest"

set -e  # stop si erreur

TAG=${1:-latest}

echo "🧩 1. Activation de l’environnement Docker Minikube..."
eval $(minikube docker-env)

echo "⚙️ 2. Build de l’image Docker challengr-webapp:$TAG ..."
cd webapp
npm run build
docker build -t challengr-webapp:$TAG .

echo "📦 3. Déploiement sur Kubernetes..."
kubectl -n challengr set image deploy/webapp web=challengr-webapp:$TAG || true
kubectl -n challengr rollout restart deploy/webapp

echo "🕒 4. Attente de la fin du déploiement..."
kubectl -n challengr rollout status deploy/webapp

echo "🔍 5. Vérification de la version déployée..."
kubectl -n challengr get deploy webapp -o=jsonpath='{.spec.template.spec.containers[*].image}'

echo "✅ Déploiement terminé avec succès !"
echo "🌐 Accès : http://challengr.local"
