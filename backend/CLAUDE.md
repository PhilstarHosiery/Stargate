# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

See the root `../CLAUDE.md` for project overview, domain concepts, and core workflows.

## Commands

### Backend (Go)

```bash
# Run (from backend/)
go run ./cmd/server
# or on Windows:
run.bat

# Build binary
go build -o stargate-server ./cmd/server
# or on Windows:
build.bat

# Regenerate gRPC code from proto/stargate.proto
make gen          # Unix
gen.bat           # Windows

# Dependency management
go mod tidy

# Run tests
go test ./...

# Run a single package's tests
go test ./internal/db/...
```

### Create the first admin user (interactive CLI prompt)

```bash
go run ./cmd/server -create-user
```

## Architecture

The backend is a single Go binary that starts two servers concurrently:
- **gRPC server** (`:50051`) — all client-facing RPCs
- **HTTP server** (`:8080`) — SMS Gate webhook endpoint

### Key files and responsibilities

| File | Responsibility |
|------|---------------|
| `cmd/server/main.go` | Entry point: wires config, DB, SMS client, gRPC server, HTTP server |
| `config/config.go` | YAML config loader (`config/config.yaml` → `config.yaml` fallback) |
| `internal/models/models.go` | Shared data structs (User, Contact, Group, Session, Message) |
| `internal/db/db.go` | All database queries; runs embedded migrations on startup |
| `internal/grpc/server.go` | Implements every gRPC RPC (`pb.StarGateCoreServer`) |
| `internal/grpc/streams.go` | `StreamManager` — thread-safe map of active `SubscribeToInbox` streams |
| `internal/sms/webhook.go` | HTTP handler for inbound SMS webhooks |
| `internal/sms/outbound.go` | Sends outbound SMS via `android-sms-gateway/client-go` |
| `gen/` | Auto-generated from `proto/stargate.proto` — do not edit manually |

### Real-time broadcast flow

`webhook.go` → `db.go` (store message) → `streams.go` `Broadcast()` → gRPC `SubscribeToInbox` streams on connected clients.

`server.go` `SendReply()` follows the same path in reverse: store outbound message → `outbound.go` `Send()` → `Broadcast()` to all *other* connected users so their UIs update.

### Access control

`UserHasAccess(userID, groupID)` in `db.go` is the single gate. A user passes if they appear in `user_groups` for that group **or** `has_global_access = true`. This is checked in both `GetSessions` and the webhook broadcast target list.

### Deduplication

`CreateMessage()` accepts an optional `gateway_message_id`. The column has a unique index; duplicate webhook deliveries are silently ignored with an `INSERT OR IGNORE`.

### Configuration

Copy `config/config.example.yaml` → `config/config.yaml`. Key fields:

```yaml
server:
  grpc_addr: ":50051"
  webhook_addr: ":8080"
database:
  path: "stargate.db"
sms:
  gate_url: "http://192.168.1.1"    # SMS Gate app / RUT241 API base URL
  username: ""                       # Local Mode basic auth (SMS Gate → Settings → Local Server)
  password: ""
  api_key: ""                        # Cloud Mode bearer token — leave empty for Local Mode
  webhook_secret: ""                 # HMAC-SHA256 signing key from SMS Gate settings
  webhook_url: "https://your-server/webhook"  # Externally-accessible URL SMS Gate will POST to
```

### Proto regeneration prerequisites

Install once via `go install`:

```bash
go install github.com/bufbuild/buf/cmd/buf@latest
go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
```

`buf generate` is driven by `buf.gen.yaml` at the repo root and `proto/buf.yaml`. No separate `protoc` binary needed.
