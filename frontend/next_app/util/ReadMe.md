This dir stores all js_out generated files to be used in frontend js applications. NEVER PUSH generated files to repo

To generate all task service proto files for example , run : 
- protoc proto/task/*.proto --js_out=import_style=commonjs,binary:frontend/genProto --grpc-web_out=import_style=commonjs,mode=grpcwebtext:frontend/genProto/