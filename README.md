# FINT FLYT INSTANCE GATEWAY TEMPLATE

## Overview
This template serves as a skeleton gateway with example code for creating specific instance-gateways in FINT Flyt. Its primary function is to handle POST requests containing data, which is then processed and published to other microservices through Kafka inside the FINT Flyt infrastructure.

## Getting Started
To use this template, you need to customize it according to your specific requirements. Follow the steps below to configure and deploy your instance of the gateway.

### Rename `sourceApplication`
The `sourceApplication` parameter represents the data source sending information to this gateway (e.g., `egrunnerverv`, `acos`, `rf13.50`, etc.). Rename it throughout the project to reflect your specific source application.

- **Example**: Rename `fint-flyt-sourceapplication-instance-gateway` to `fint-flyt-rf1350-instance-gateway`.

### Kustomize Configuration
Adjust the following setting in the `base/flais.yaml`:
**`fint.flyt.resource-server.security.api.external.authorized-client-ids`**:
   - Set the `sourceApplicationId` provided by `fint-flyt-authorization-service` (the value should be an integer).
   - it ensures that requests coming from a specific client-id will only be allowed in if the ID it what `fint-flyt-authorization-service` says it should be.
   - this ID will be passed through FINT Flyt to ensure the data package received by this gateway belongs to the given SourceApplication

### Application Configuration
Configure the application settings in `src/main/resources/application-local-staging.yaml`:
  - **`server.port`**:
  - When testing locally, select a server port in the range reserved for instance-gateways (8100-8199)

## Additional Documentation
For more detailed instructions on setting up and deploying your instance of the FINT FLYT gateway, refer to the documentation:
https://fintlabs.atlassian.net/wiki/spaces/FINTKB/pages/379355169/FINT+Flyt+Arkitektur+event+driven

This is also where you document the SourceApplication (data models, requests, endpoints etc)
