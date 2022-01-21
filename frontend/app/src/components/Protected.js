import useAuthData from '../hooks/useAuthData'
import React, { useState, useEffect } from 'react';
import {getUrl} from '../protoHelper';

export default function Protected(props){

	const [loadingState, accessToken] = useAuthData();

	useEffect(() => {
		console.log("protected ")
		console.log(loadingState)
	},[loadingState])

	console.log(loadingState)
	if(loadingState == "SUCCESS" || (loadingState == "IN_PROGRESS" && accessToken != null) ){
		return (props.children)
	}else if(loadingState == "FAILED"){
		window.location.assign(getUrl());
		return "FAILED"
	}
	else{
		return "LOADING..."
	}

}