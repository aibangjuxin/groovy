#!/bin/bash
# kubectl get all sa . then describe the sa annotations include keyword "kubernetes.io/service-account.name"
kubectl get sa --all-namespaces -o json | jq -r '.items[] | select(.metadata.annotations."kubernetes.io/service-account.name") | .metadata.annotations'
kubectl get sa --all-namespaces -o json | jq -r '.items[] | select(.metadata.annotations."kubernetes.io/service-account.name") | .metadata.annotations."kubernetes.io/service-account.name"'

# get gcp service account email and project id 
gcloud iam service-accounts list --filter="email LIKE '%@%'" --format="value(email)"