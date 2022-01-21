import React, { useState, useEffect } from 'react';
import {gatewayUrl} from '../util/url'
import axios from 'axios';

var jwt = require("jsonwebtoken");

export default function useAuthData(test = null){

	const [accessToken, setAccessToken] = useState(null);

	// IN_PROGRESS, SUCCESS, FAILED
	const [loadingState, setLoadingState] = useState(null);


	console.log()
	// after initial render set loading state to IP
	useEffect(() => {
		console.log("useAuth initial")
		setLoadingState("IN_PROGRESS");
	},[])

	useEffect(() => {
		if(loadingState === 'IN_PROGRESS'){
			console.log("useAuth fetch")
			console.log(gatewayUrl())
			// make call to fetch new access token
			axios.post(gatewayUrl()+"/gateway/refresh")
				.then(res => {
					console.log(res.data)
					if(res.data.access_token != null){
						setAccessToken(res.data.access_token);
					}else{
						console.log("failed auth")
						//setLoadingState("FAILED")
					}
				})
				.catch(function (error){
					console.log(error)

					//setLoadingState("FAILED")
				})
		}
	}, [loadingState])

	useEffect(() =>{
		console.log("useAuth ac")
		console.log(accessToken)
		if(accessToken != null){
			console.log("useAuth set access")
			setLoadingState("SUCCESS");
			console.log((jwt.decode(accessToken)['iat'] - new Date().getTime()/1000 - 200) * 10)
			// parse access token iat, refetch 200 millis before expiration
			setTimeout(
				(jwt.decode(accessToken)['iat'] - new Date().getTime()/1000 - 200) * 100
				,() =>  setLoadingState("IN_PROGRESS"))
		}
	}, [accessToken])

	return [loadingState, accessToken];
}