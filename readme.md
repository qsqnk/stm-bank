# STM-Bank

Implemented lock-free transaction memory model and REST API Bank with [Ktor](https://github.com/ktorio/ktor)

## Build and execuiton

Build: ```./gradlew buld```

Run: ```./gradlew run```

Run tests: ```./gradlew test```

## Overview

### Sign up and sign in

##### Sign up

```
curl --header "Content-Type: application/json" \
--request POST \
--data '{"login":"qsqnk","password":"12345"}' \
http://localhost:8080/signup
```

##### Sign in

HTTP Basic Auth

### Bank operations

##### Get balance

```
curl -u login:password \
--request GET \
http://localhost:8080/bank/balance
```

##### Top up

```
curl -u login:password \
--header "Content-Type: application/json" \
--request POST \
--data '{"amount":100}' \
http://localhost:8080/bank/topup
```

##### Withdraw

```
curl -u login:password \
--header "Content-Type: application/json" \
--request POST \
--data '{"amount":100}' \
http://localhost:8080/bank/withdraw
```

##### Transfer

```
curl -u login:password \
--header "Content-Type: application/json" \
--request POST \
--data '{"to": "some_user", "amount":100}' \
http://localhost:8080/bank/transfer
```
