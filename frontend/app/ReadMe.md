##Frontend App 

#Run webapp locally 
- generate js proto/grpc files : protoc proto/DIRECTORY_PATH*.proto --js_out=import_style=commonjs,binary:frontend/genProto/ --grpc-web_out=import_style=commonjs,mode=grpcwebtext:frontend/genProto/

- run local cluster & expose envoy service : spec_local/startup.sh && minikube service envoy -n slot
- run webpack dev locally w/ webpack.config.js : LOCAL_URL=ENVOY_SERVICE_URL npm run dev 

#Run Dev server
- ibazel run //frontend/app:dev_server --REACT_APP_BASE_URL=<URL FOR BACKENDS> 

#Run Prod Server
- http local : ibazel run //frontend/app:dev_server --REACT_APP_BASE_URL=<URL FOR BACKENDS> 
- docker image gen : bazel run //frontend/app:prod_image