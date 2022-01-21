const LOCAL_PATH = './genProto/proto/task/'
const BAZEL_PATH = './proto/task/'

const PATH = process.env.LOCAL_URL ? LOCAL_PATH : BAZEL_PATH

const taskServiceProto = require(PATH+'task_service_grpc_web_pb.js')

var getUrl = (process.env.LOCAL_URL ? process.env.LOCAL_URL : "PH_REACT_APP_BASE_URL");

module.exports = {
	getUrl: () => getUrl,
	getTaskServiceProto: () => taskServiceProto,
	getTaskServiceClient: () => new taskServiceProto.TaskServiceClient(
			getUrl + "/gapi", null, {'withCredentials': true})
}