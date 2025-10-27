cd webapp
TAG="v$(date +%y%m%d%H%M)"
docker build -t webapp:$TAG . || exit 1
minikube image load webapp:$TAG || exit 1
kubectl set image deployment/webapp webapp=webapp:$TAG -n challengr
kubectl rollout status deployment/webapp -n challengr
kubectl -n challengr get pods -l app=webapp
