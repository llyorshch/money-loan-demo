#!/bin/bash

FLOWABLE_HOST=localhost
FLOWABLE_PORT=8080
FLOWABLE_REST_USER=rest-admin
FLOWABLE_REST_PASSWORD=test

function checkCommand {
    if ! [ -x "$(command -v $1)" ]; then
        echo "Error: $1 is not installed." >&2
        exit 1
    else
        echo "$1 is installed."
    fi
}

checkCommand "curl"
checkCommand "jq"

# Users
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "id": "rafa", "firstName": "Rafa", "lastName": "el cajero", "email": "rafa@banco.com", "password": "test" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/users
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "id": "nuria", "firstName": "Nuria", "lastName": "la interventora", "email": "nuria@banco.com", "password": "test" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/users

# Groups
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "id": "bankers", "name": "Bankers", "type": "assignment" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "id": "bankTellers", "name": "Bank Tellers", "type": "assignment" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "id": "loanReviewers", "name": "Loan Reviewers", "type": "assignment" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups

# Users to groups
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "userId": "rafa" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups/bankers/members
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "userId": "nuria" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups/bankers/members

curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "userId": "rafa" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups/bankTellers/members
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "userId": "nuria" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/service/identity/groups/loanReviewers/members

# Privileges to access the workflow application
PRIVILEGE_ID=$(curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X GET --header "Accept: application/json" http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/idm-api/privileges?name=access-task | jq ".data[0].id" | tr -d '"')
curl --silent --show-error -u $FLOWABLE_REST_USER:$FLOWABLE_REST_PASSWORD -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "groupId": "bankers" }' http://$FLOWABLE_HOST:$FLOWABLE_PORT/flowable-rest/idm-api/privileges/$PRIVILEGE_ID/groups
