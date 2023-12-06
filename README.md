# FINT FLYT INSTANCE GATEWAY TEMPLATE

## Rename sourceApplication to <name of sourceApplication> everywhere
### SourceApplication is the source thats sends data to this gateway (ex. egrunnerverv, acos, rf13.50 etc)
### Ex: 'fint-flyt-sourceapplication-instance-gateway' -> 'fint-flyt-rf1350-instance-gateway'

test

## Kustomize
```shell
    base/flais.yaml -> 
      set 'fint.flyt.resource-server.security.api.external.authorized-client-ids' to the sourceApplicationId that fint-flyt-authorization-service provides
      should be an integer
    overlays/<orgId>/<env>/kustomization -> 
      set correct basePath. URL basePath and ingress basePath should not be the same
```

## Application
```shell
    src/main/resources/application-local-staging.yaml
      server.port -> when testing locally, should use a server port that is not in use by other flyt applications 
```
