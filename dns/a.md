#!/bin/bash
team_name="teamname"
project=aibang-111111111-aibangjuxin-dev
ENV=dev
REGION=uk
AUTHN_FQDN=auths.$ENV-sb-rt.gcp.cloud.$REGION.local
if [[ $REGION == hk || $REGION == uk ]]; then AUTHN_FQDN=auths.$ENV-sb-rt.gcp.cloud.$REGION.aibang; fi
# private-dev-gke-cloud-uk-local
# define a scoped project for the DNS zone
# create a DNS zone for the private environment
zone_name="private-$ENV-gke-cloud-$REGION-$team_name-local"
if [[ $REGION == hk || $REGION == uk ]]; then zone_name="private-$ENV-gke-cloud-$REGION-$team_name-aibang"; fi

echo $AUTHN_FQDN
echo $zone_name

description="$zone_name private zone for $team_name gke"
# the next will create a DNS zone for the private environment
echo "gcloud dns --project=$project managed-zones create $zone_name --description="\"${description}\"" --dns-name=$zone_name --dns-name=$AUTHN_FQDN -- visibility=private --networks="https://compute.googleapis.com/compute/v1/projects/aibang-111111111-aibangjuxin-dev/global/networks/aibang-111111111-aibangjuxin-dev-cinternal-vpc2""


# the next will create a DNS record for the private environment
echo "gcloud dns --project=$project record-sets create $AUTHN_FQDN --zone=$zone_name --type=A --ttl=30 --rrdatas=10.10.10.10"
