##Frontend Apps

React app will be protectd by access token rotation

For non k8s testing: 

**NOTE** : there may be complication's w/ user auth if running outside of k8s cluster, might need to add signal in backend to not validate 

1) generate the proto js files, will need to install `protoc` 
 - use `genProto` dir in react app `util` to store all the files

Example proto generation: 
protoc proto/DIRECTORY_PATH*.proto --js_out=import_style=commonjs,binary:frontend/app/util/genProto/ --grpc-web_out=import_style=commonjs,mode=grpcwebtext:frontend/app/util/genProto/


2)run local cluster & expose envoy service : spec_local/startup.sh && minikube service envoy -n slot

3)run webpack dev locally w/ webpack.config.js : LOCAL_URL=ENVOY_SERVICE_URL npm run dev 

#Run Dev server
- ibazel run //frontend/app:dev_server --REACT_APP_BASE_URL=<URL FOR BACKENDS> 

#Run Prod Server
- http local : ibazel run //frontend/app:dev_server --REACT_APP_BASE_URL=<URL FOR BACKENDS> 
- docker image gen : bazel run //frontend/app:prod_image


