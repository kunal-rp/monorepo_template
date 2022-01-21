

module.exports = {
	gatewayUrl: () => process.env.LOCAL_URL != undefined ? process.env.LOCAL_URL : "PH_REACT_APP_BASE_URL"
}