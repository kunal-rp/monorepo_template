
var isLocal = process.env.NODE_ENV == "development"

module.exports = {
	omitAuthCalls: () => process.env.OMIT_GATEWAY_AUTH_CALL == "true", 
	getUrl: () => (process.env.NEXT_PUBLIC_REACT_APP_BASE_URL != undefined ? process.env.NEXT_PUBLIC_REACT_APP_BASE_URL : "PH_NEXT_PUBLIC_REACT_APP_BASE_URL")

}