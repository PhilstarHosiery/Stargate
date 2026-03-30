@echo off
:: Generate Go code from proto/stargate.proto into gen/
::
:: Requires (install once):
::   go install github.com/bufbuild/buf/cmd/buf@latest
::   go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
::   go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

if not exist gen mkdir gen

pushd ..
buf generate
if %ERRORLEVEL% neq 0 (
    popd
    echo.
    echo ERROR: buf generate failed. Make sure all three tools are installed:
    echo   go install github.com/bufbuild/buf/cmd/buf@latest
    echo   go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
    echo   go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
    exit /b 1
)
popd

:: Remove the stub file now that real generated code exists.
if exist gen\stub.go del gen\stub.go

echo Done. Generated files are in gen\
