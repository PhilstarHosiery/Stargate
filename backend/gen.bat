@echo off
:: Generate Go code from proto/stargate.proto into gen/
::
:: protoc is bundled at ..\bin\protoc.exe (protoc 34.0)
:: Go plugins must be installed:
::   go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
::   go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

if not exist gen mkdir gen

..\bin\protoc.exe ^
  --proto_path=..\proto ^
  --go_out=gen ^
  --go_opt=paths=source_relative ^
  --go-grpc_out=gen ^
  --go-grpc_opt=paths=source_relative ^
  ..\proto\stargate.proto

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: protoc failed. Make sure protoc-gen-go and protoc-gen-go-grpc are installed and on PATH.
    exit /b 1
)

:: Remove the stub file now that real generated code exists.
if exist gen\stub.go del gen\stub.go

echo Done. Generated files are in gen\
