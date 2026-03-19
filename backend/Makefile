# StarGate Backend
#
# protoc is bundled at ../bin/protoc (download a FreeBSD/Linux build for production).
# Go plugins must be installed:
#   go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
#   go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

PROTOC ?= ../bin/protoc

.PHONY: gen build run tidy

gen:
	mkdir -p gen
	$(PROTOC) \
		--proto_path=../proto \
		--go_out=gen \
		--go_opt=paths=source_relative \
		--go-grpc_out=gen \
		--go-grpc_opt=paths=source_relative \
		../proto/stargate.proto
	rm -f gen/stub.go

build:
	go build -o stargate-server ./cmd/server

run:
	go run ./cmd/server

tidy:
	go mod tidy
