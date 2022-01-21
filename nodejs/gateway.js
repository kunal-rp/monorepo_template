const grpc = require('@grpc/grpc-js');
const express = require('express')
const cookieParser = require('cookie-parser');
var bodyParser = require('body-parser')

const taskServiceProto = require('./task_service_proto_pb/proto/task/task_service_grpc_pb.js')
const taskProto = require('./task_service_proto_pb/proto/task/task_service_pb.js')

const userProto = require('./user_js_proto_pb/proto/user/user_pb.js')
const userServiceProto = require('./user_management_service_proto_pb/proto/user/user_management_service_pb.js')
const userServiceClientProto = require('./user_management_service_proto_pb/proto/user/user_management_service_grpc_pb.js')


// create application/json parser
var jsonParser = bodyParser.json()

const app = express()
const port = 3000
const REFRESH_COOKIE_KEY = "slot_refresh"
const SITE_COOKIES = [
  REFRESH_COOKIE_KEY,
  "next-auth.session-token",
  "next-auth.callback-url",
  "next-auth.csrf-token",
]


var client = new taskServiceProto.TaskServiceClient("task:80", grpc.credentials.createInsecure()); 
var userClient = new userServiceClientProto.UserManagementServiceClient("user-management:80", grpc.credentials.createInsecure());

app.use(cookieParser());

app.get('/', (req, res) => {
  res.send('this is the GATEWAY!')
})

var actionRequest = () => new taskProto.ActionRequest();

var refreshRequest = (existingRefreshToken) => 
    new userServiceProto.RegenerateRefreshTokenRequest()
        .setExistingRefreshToken(new userProto.UserRefreshToken().setData(existingRefreshToken));

var signInRequest = (idToken) => 
    new userServiceProto.SignInRequest()
        .setIdToken(idToken);


app.get('/testgrpc', (req, res) => {
    var request = actionRequest()
    client.someAction(request, (err, data) => {
      if(err){
        res.json({err: err})
        return
      }
      res.cookie('custom_slot_header', 'fromTestGrpc',{httpOnly:true}) 
      res.json({data : data.toObject()})
    })
  })

app.post('/refresh', (req, res) => {

  console.log("refresh")
  console.log(req.cookies)
  if(req.cookies[REFRESH_COOKIE_KEY] == null){
    res.json({err:"Invalid refresh"})
    console.log('invalid refresh')
    return
  }

  userClient.regenerateRefreshToken(refreshRequest(req.cookies[REFRESH_COOKIE_KEY]), (err, data) => {
    if(err){
        res.json({err: err})
        return
      }
    res.cookie(REFRESH_COOKIE_KEY, data.getRefreshToken().getData(),{httpOnly:true});
    res.json({access_token: data.getAccessToken().getData()})
  })
})

app.post('/signIn',jsonParser,  (req, res) => {

  console.log("signin")

  if(req.body.id_token == null){
    res.json({err:"Invalid Id Token"})
    return
  }

  userClient.signIn(signInRequest(req.body.id_token), (err, data) => {
    if(err){
        res.json({err: err})
        return
      }
    res.json({
      access_token: data.getAccessToken().getData(),
      refresh_token:data.getRefreshToken().getData()})
  })
})

app.get('/signOut', (req, res) => {
  SITE_COOKIES.forEach(cook => res.clearCookie(cook))
  res.redirect(200, process.env.REACT_APP_BASE_URL);
})

app.listen(port, () => {
  console.log("gateway server running")
})


function getRand(min, max) {
  return Math.trunc(Math.random() * (max - min) + min);
}

