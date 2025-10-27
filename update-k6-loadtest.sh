cd k8s
kubectl -n challengr delete job k6-loadtest
kubectl apply -f k6-loadtest.yaml
kubectl -n challengr logs -f job/k6-loadtest
