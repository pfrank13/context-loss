# context-loss
Small Spring Boot project to show losing context in variour Reactive situations

## To Standup and Test
1. `./gradlew clean build bootRun`
1. `GET http://localhost:8080'
   1. `/suspend/loss` will execute losing context from a `Scheduler` to Kotlin `Dispatcher`
   1. `/suspecnd/noLoss` will execute without losing context as it will be "pushed" into the coroutineScope
   1. `/executor/loss` will execute losing context from a `Scheduler` to a `Executor`
