# hse-26-summer

Distributed Systems lecture collected material, summaries and questions.
Also this repository will hold example code.

## Distributed Systems - 19.03.26

General introduction into

- what a distributed system is
- advantages and disadvantages
- how it relates to cloud computing
- Service Models: IaaS, PaaS, SaaS

![MindMap](https://github.com/user-attachments/assets/40db9f01-5fc7-4701-9ec7-7ba523dc384a)

![Provisioning](https://github.com/user-attachments/assets/e2139d80-21c5-48b2-ac28-0daf6fbebe25)

## Cloud Native Development - 27.03.26

Introduction into the developers perspective of the cloud world

- Pillars of Cloud Native Development
- Microservices
- Staging
- Scaling
- CAP theorem
- Conways law
- 12 Factor Apps

![Staging](https://github.com/user-attachments/assets/6466ae7d-31cb-4993-a5ab-b2b771044906)

## Cloud Native Development in Practice - 10.04.26

Introduction into the practical side of cloud native development:

- Frameworks
  - General Idea
  - Benefits
  - Spring Boot
  - Spring ecosystem
- Interservice Communication
  - Synchronous vs Asynchronous Communication
  - REST
  - Resources, Verbs and Representations
  - Richardson Maturity Model

### Questions for Exam Preparation

- With the basic Rest Controller having a local `ArrayList` as storage of `TodoItem`s: what are potential issues in the long run, where does this conflict with concepts we learned about?

![service communication](images/service-communication.png)


## Containerisation

Introduction into packaging applications as containers:

- Problem with running applications directly on a host
  - different machines may have different Java versions, tools or system libraries
  - manual setup is hard to reproduce
  - deployments become dependent on the local environment
  - scaling and replacing instances becomes harder
- Container image
  - packaged application plus runtime dependencies
  - built once and started many times
  - usually created from a `Dockerfile` or build tool plugin
  - consists of layers that can be cached and reused
- Container
  - running instance of an image
  - isolated process with its own filesystem, network view and environment
  - can be started, stopped, removed and recreated
- Dockerfile
  - describes how to build an image
  - chooses a base image
  - copies the application artifact
  - defines the command used to start the application
- Ports and networking
  - Spring Boot usually listens on port `8080` inside the container
  - host-to-container port mapping makes the application reachable from outside
  - containers can communicate through container networks
- Configuration
  - environment variables are commonly used for runtime settings
  - datasource URLs, active profiles and secrets should not be hardcoded into the image
  - same image can be used in different environments with different configuration
- Container registry
  - storage location for container images
  - examples are Docker Hub, GitHub Container Registry or a private registry
  - deployment platforms pull images from registries
- Cloud native perspective
  - containers support reproducible deployment
  - application instances should be stateless where possible
  - persistent state belongs in external databases or managed storage
  - containers are the basis for orchestration platforms like Kubernetes

Basic example workflow:

```bash
docker build -t starterapp .
docker run --rm -p 8080:8080 starterapp
```

### Questions for Exam Preparation

- What problem does containerisation solve compared to installing an application directly on a server?
- What is the difference between a container image and a running container?
- What is the purpose of a `Dockerfile`?
- Why should a container image not contain environment-specific secrets or database URLs?
- What does port mapping do when running a web application in a container?
- Why are containers often described as disposable?
- What happens to data stored only inside a container when the container is removed?
- Why should persistent state usually be stored outside the application container?
- What is a container registry, and why is it useful in deployment pipelines?
- How does containerisation support cloud native principles such as scalability and reproducibility?

## Persistence - 08.05.26

Introduction into moving the TODO application from local memory towards persistence:

- Problem with local `ArrayList` storage
  - data is lost after restart
  - state is tied to one application instance
  - multiple replicas would not share the same data
  - controller mixes HTTP handling and storage logic
- Persistence
  - storing data beyond the lifetime of one process
  - keeping application instances mostly stateless
  - moving durable state into a database
- Spring Boot application structure
  - `TodoController` handles HTTP requests and REST status codes
  - `TodoService` contains application logic
  - `TodoRepository` handles database access
  - `TodoItem` represents persistent data as a JPA entity
- JDBC
  - low-level Java API for database connections, SQL statements and result sets
  - powerful but often verbose when used directly
- JPA and Hibernate
  - JPA maps Java objects to relational database tables
  - Hibernate is the JPA implementation used by Spring Boot
  - `@Entity`, `@Id` and `@GeneratedValue` describe how `TodoItem` is stored
- Spring Data JPA
  - repository abstraction on top of JPA
  - provides methods like `findAll`, `findById`, `save`, `deleteById` and `existsById`
  - reduces boilerplate persistence code
- H2 database
  - lightweight database for local development and tests
  - file-based H2 can keep local application data
  - in-memory H2 is useful for isolated automated tests
- Production perspective
  - external databases such as PostgreSQL or MySQL are more realistic for deployed systems
  - datasource configuration can be changed without rewriting the controller or service layer

Basic repository example:

```java
public interface TodoRepository extends JpaRepository<TodoItem, Integer> {
}
```

### Questions for Exam Preparation

- Why is local in-memory state problematic when an application is restarted, redeployed, or scaled to multiple instances?
- What is the responsibility of a controller, service, repository, and entity in a typical Spring Boot application?
- What problem does persistence solve compared to storing data in a local Java collection?
- What does `JpaRepository<TodoItem, Integer>` provide automatically?
- Why should a database usually be external to application instances in a cloud native or distributed system?
- What is the difference between using H2 for local development or tests and using a production database such as PostgreSQL?
- Why is it useful to keep tests on an in-memory database instead of the application's file-based local database?

## Containers Extended and Persistence for Containers - 15.05.26

In this session we extended the persistance capabilites of the TODO application by moving from a local H2 database to a PostgreSQL database running in a separate container. The application and the database were connected through environment variables provided through application configration and the Docker run command.

### Running PostgreSQL as a container

PostgreSQL can be started as a separate container:

```bash
docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_USER=postgres -e POSTGRES_DB=postgres -d postgres:14.19
```

The command above starts PostgreSQL and publishes port `5432` to the host. This is useful when the application runs directly on the host or when we want to connect with a local database client.

This is closer to a real distributed application because the application process and the database process run separately and communicate over the network.

### Containerising the Spring Boot application

The Spring Boot application in `starterapp/` was packaged as a container image. The repository contains two Dockerfiles to show the difference between a minimal teaching example and a more production-oriented build.

- `starterapp/Dockerfile`
  - minimal example
  - expects the `.jar` file to be built before the Docker image is built
  - copies only the prebuilt jar into a Java runtime image
  - easy to understand, but the build still depends on the host machine having Maven and the correct Java version
- `starterapp/Dockerfile.multistage`
  - best-practice example
  - builds the jar inside a builder stage
  - copies only the final jar into a smaller runtime stage
  - avoids shipping Maven, source code and build output in the final image
  - runs the application as a non-root user

Build the simple image from a prebuilt jar:

```bash
cd starterapp
./mvnw package
docker build -t starterapp:simple .
```

Build the multistage image:

```bash
cd starterapp
docker build -f Dockerfile.multistage -t starterapp:multistage .
```

Run the application image and connect it to PostgreSQL running on the host:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=mysecretpassword \
  starterapp:simple
```

Important idea: `localhost` inside a container means "inside this same container". It does not mean the host machine and it does not mean another container.

For a more realistic setup, the database should use a volume so data survives when the container is removed:

```bash
docker volume create postgres-data

docker run --name some-postgres \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=postgres \
  -v postgres-data:/var/lib/postgresql/data \
  -d postgres:14.19
```

### Volumes

Containers are designed to be disposable. If important data is written only into the container filesystem, that data can disappear when the container is removed and recreated.

A Docker volume stores data outside the lifecycle of one specific container.

- named volumes are managed by Docker
- bind mounts map a specific host directory into a container
- database containers usually use volumes for persistent data
- removing a container does not automatically remove its named volumes
- removing a volume deletes the persisted data stored in that volume

Useful volume commands:

```bash
docker volume ls
docker volume inspect postgres-data
docker volume rm postgres-data
```

### Networks

Containers have isolated network environments. A container has its own `localhost`, its own filesystem and its own process view.

Docker can connect containers through container networks. On a user-defined Docker network, containers can reach each other by container name.

Example with an explicit network:

```bash
docker network create todo-net

docker run --name postgres \
  --network todo-net \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=postgres \
  -v postgres-data:/var/lib/postgresql/data \
  -d postgres:14.19

docker run --rm --name starterapp \
  --network todo-net \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=mysecretpassword \
  starterapp:simple
```

In this example, the application uses `postgres` as the database hostname because `postgres` is the container name on the shared Docker network.

Important networking distinction:

- `localhost` from the host means the host machine
- `localhost` from the Spring Boot container means the Spring Boot container itself
- `postgres` from the Spring Boot container means the PostgreSQL container, if both containers are on the same user-defined network

### Docker Compose

Docker Compose describes a multi-container setup in one YAML file. Instead of manually creating networks, volumes and containers with several `docker run` commands, Compose lets us define the desired application stack declaratively.

Compose is useful for local development and teaching because it shows the complete system in one place:

- services define the containers
- ports define host-to-container port mappings
- environment variables provide runtime configuration
- volumes persist data outside containers
- networks allow services to communicate

Example `compose.yaml`:

```yaml
services:
  postgres:
    image: postgres:14.19
    environment:
      POSTGRES_DB: postgres
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: mysecretpassword
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build:
      context: ./starterapp
      dockerfile: Dockerfile.multistage
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/postgres
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: mysecretpassword
    depends_on:
      - postgres

volumes:
  postgres-data:
```

Start the full stack:

```bash
docker compose up --build
```

Stop and remove the containers:

```bash
docker compose down
```

Stop and remove the containers plus the database volume:

```bash
docker compose down -v
```

`depends_on` controls startup order, but it does not guarantee that PostgreSQL is fully ready to accept connections. Real systems should handle startup timing with retries, health checks or application-level resilience.

### Questions for Exam Preparation

- Why does `localhost` mean different things on the host and inside a container?
- Why should a database container use a volume?
- What is the difference between a named volume and a bind mount?
- What problem does a Docker network solve when multiple containers need to communicate?
- Why can the application use `postgres` as a hostname in a Compose setup?
- What is the benefit of Docker Compose compared to several manual `docker run` commands?
- What is the difference between the simple Dockerfile and the multistage Dockerfile?
- Why is it better if the final runtime image does not contain Maven, source files or build artifacts?
- Why should application configuration such as database URLs and passwords be passed through environment variables?

## Resilience

Introduction into resilience in cloud native applications, Spring Boot applications and container-based deployments:

- Resilience
  - ability of a system to keep working, recover, or degrade gracefully when something fails
  - important because distributed systems depend on networks, databases, external services and infrastructure
  - failures are expected events, not exceptional surprises
- Typical failure scenarios
  - service instance crashes or is restarted
  - database is temporarily unavailable
  - network request is slow, lost or returns an error
  - container is removed and recreated
  - one downstream service is overloaded
- Cloud native resilience
  - applications should be horizontally scalable
  - instances should be disposable and replaceable
  - configuration should come from the environment
  - persistent state should be stored outside application containers
  - services should tolerate restarts and temporary dependency failures
- Resilience patterns
  - timeouts prevent clients from waiting forever
  - retries can handle temporary failures, but should be limited
  - circuit breakers stop repeated calls to a failing dependency
  - bulkheads isolate failures so one problem does not consume all resources
  - fallbacks can return reduced functionality when a dependency is unavailable
- Spring Boot perspective
  - health endpoints can expose whether an application is alive and ready
  - Actuator provides operational endpoints such as health and metrics
  - datasource configuration and profiles should be externalized
  - startup should handle dependencies that may not be ready immediately
  - application logic should not assume that every remote call succeeds
- Containers and resilience
  - containers can be stopped, replaced and recreated
  - data inside the container filesystem is not a reliable persistence strategy
  - container images should be reproducible and environment-independent
  - orchestration platforms can restart failed containers, but the application must still handle failures correctly
- Important distinction
  - high availability means the service should remain reachable
  - fault tolerance means parts of the system can fail without breaking the whole system
  - resilience includes detection, recovery and graceful degradation

### Questions for Exam Preparation

- Why is failure considered normal in distributed and cloud native systems?
- What is the difference between availability, fault tolerance and resilience?
- Why should application instances be stateless where possible?
- Why can a retry help with temporary failures, and why can uncontrolled retries make a system worse?
- What problem does a timeout solve when calling another service?
- What is the purpose of a circuit breaker?
- Why should persistent data not be stored only inside an application container?
- How can Spring Boot Actuator help with operating a service in containers?
- Why does an application still need resilience logic if containers can be restarted automatically?
- What could happen if all requests wait forever for a slow downstream service?
