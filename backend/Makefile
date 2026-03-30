# StarGate Backend
#
# Requires (install once):
#   go install github.com/bufbuild/buf/cmd/buf@latest
#   go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
#   go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

.PHONY: gen build run tidy

gen:
	mkdir -p gen
	cd .. && buf generate
	rm -f gen/stub.go

build:
	go build -o stargate-server ./cmd/server

run:
	go run ./cmd/server

tidy:
	go mod tidy
