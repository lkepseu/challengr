#!/bin/bash
set -e  # ⛔️ stop immediately if a command fails

# ===========================
# 🔧 Variables dynamiques
# ===========================
SERVICE=$1
if [ -z "$SERVICE" ]; then
  echo "❌ Usage: $0 <service-name>"
  echo "   Example: ./redeploy-service.sh submission-service"
  exit 1
fi

TAG="v$(date +%Y-%m-%d-%Hh%Mm)"
NAMESPACE="challengr"
YAML_FILE="$SERVICE.yaml"

# ===========================
# 🚀 Étapes
# ===========================
echo "=========================================="
echo "🔁 Redeploying service: $SERVICE ($TAG)"
echo "=========================================="

# 1️⃣ Compiler et packager
echo "📦 Building Java project for $SERVICE..."
cd $SERVICE
mvn -q -DskipTests clean package || { echo "❌ Maven build failed"; exit 1; }

# 2️⃣ Construire l'image Docker
echo "🐳 Building Docker image: $SERVICE:$TAG ..."
docker build -t $SERVICE:$TAG . || { echo "❌ Docker build failed"; exit 1; }

# 3️⃣ Charger l’image dans Minikube
echo "📤 Loading image into Minikube..."
minikube image load $SERVICE:$TAG || { echo "❌ Minikube image load failed"; exit 1; }

# 4️⃣ Mettre à jour l’image dans le manifest Kubernetes
echo "🧩 Updating image tag in $YAML_FILE..."
cd ..
cd k8s
if [ -f "$YAML_FILE" ]; then
  # Remplacer l'ancien tag par le nouveau (si présent)
  sed -i.bak "s|\(image:.*$SERVICE:\).*|\1$TAG|" "$YAML_FILE"
else
  echo "⚠️ Warning: $YAML_FILE not found. Skipping YAML update."
fi

# 5️⃣ Appliquer le manifest
echo "⚙️ Applying Kubernetes manifest..."
kubectl apply -f "$YAML_FILE" -n "$NAMESPACE" || { echo "❌ kubectl apply failed"; exit 1; }

# 6️⃣ Redémarrer le déploiement
echo "🚀 Restarting deployment..."
kubectl -n "$NAMESPACE" rollout restart deployment/"$SERVICE"

# 7️⃣ Attendre que le pod soit prêt
echo "⏳ Waiting for deployment to be ready..."
kubectl -n "$NAMESPACE" rollout status deployment/"$SERVICE"

# 8️⃣ Vérifier les pods actifs
echo "📦 Pods currently running:"
kubectl -n "$NAMESPACE" get pods -l app="$SERVICE"

# 9️⃣ Afficher les logs
echo "🧠 Displaying logs..."
sleep 20
kubectl -n "$NAMESPACE" logs -l app="$SERVICE" --tail=40 -f
