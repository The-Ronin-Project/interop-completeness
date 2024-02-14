# interop-completeness

### M1/M2 Pre-steps for Integration Tests

Use brew and software update to install all the required emulation (probably already have docker and docker-compose).

```shell
brew install docker
brew install docker-compose
brew install colima

softwareupdate --install-rosetta
```

Start the compatible VM with x86_64 using

```shell
colima start --cpu 4 --memory 16 --arch x86_64 --vm-type vz --vz-rosetta
```

You should now be good to run integration tests through IntelliJ and Gradle.

### Additional Documentation Links

- [Confluence - Design](https://projectronin.atlassian.net/wiki/spaces/ENG/pages/2162753606/Interops+Data+Completeness+Service+Design)
- [Jira - Epic](https://projectronin.atlassian.net/browse/INT-2437)
- [Jira - GraphQL API definition](https://projectronin.atlassian.net/browse/INT-2460)
- [Jira - Directed Acyclic Graph (DAG) DB definition](https://projectronin.atlassian.net/browse/INT-2461)
- [Jira - Runs & Loads DB definition](https://projectronin.atlassian.net/browse/INT-2469)
- [Github - Event Contracts](https://github.com/projectronin/contract-event-interop-completeness)
