# StarGate

Centralized, real-time SMS Gateway for Philstar. Remote employees text a central number; messages are routed to the appropriate Supervisors, Managers, or HR via a JavaFX desktop client.

## Repository Layout

```
Stargate/
├── proto/          # Shared gRPC contract (stargate.proto)
├── backend/        # Go server (webhook receiver + gRPC server)
├── client/         # JavaFX desktop client (Gradle)
├── scripts/        # Build and deployment scripts
└── docs/           # Architecture notes
```

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 25 |
| Go | 1.22+ |
| Gradle | via wrapper (`./gradlew`) |

## Building

### Client (JavaFX)

```bash
cd client

# Run locally
./gradlew run

# Build jlink runtime image
./gradlew jlink

# Build native installer (exe)
./gradlew jpackage
```

Output: `client/build/image/` (runtime image), `client/build/jpackage/` (installer).

### Backend (Go)

```bash
cd backend

# Run
go run ./cmd/server

# Build
go build -o stargate-server ./cmd/server
```

### Generating gRPC Code

Proto changes must be re-compiled for both languages.

**Java** — automatic on build:
```bash
cd client && ./gradlew generateProto
```

**Go:**
```bash
cd proto
protoc --go_out=../backend --go-grpc_out=../backend stargate.proto
```

## Configuration

Backend config lives in `backend/config/`. Copy the example and edit:
```bash
cp backend/config/config.example.yaml backend/config/local.yaml
```
`local.yaml` is git-ignored.
