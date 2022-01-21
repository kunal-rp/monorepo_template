import React, {useState, useEffect} from 'react';
import Button from '@material-ui/core/Button';
import {getUrl, getTaskServiceProto, getTaskServiceClient} from './protoHelper';
import useAuthData from './hooks/useAuthData'
import {useNavigate} from 'react-router-dom'

const taskServiceProto = getTaskServiceProto()
const client = getTaskServiceClient(); 


const HelloWorld = () => {

	const [dataList, setDataList] = useState([]);
	const [loadingState, accessToken] = useAuthData();
	const navigate = useNavigate();

	useEffect(() => {

	});

	function getData(){
		var request = new taskServiceProto.ActionRequest();

		client.someAction(request, {"slot-a-tkn": accessToken},(err, data) => {
		      if(err){
		        console.log(err)
		        return
		      }
		      console.log("testgrpc response")
		      setDataList(data.getResultDataList())
		    })
	}
	
	function getDataList(){
		return dataList.map(data => <div>{data}</div>  )
	}

  return (
  		<div> 
	     	<h3> React App - Private and within user session </h3>
	     	<button onClick={() => {
	     		fetch(getUrl() + '/gateway/signOut');
	     		window.location.assign(getUrl());
	     		}
	     	}> Sign Out </button>
	     	<span style={{ cursor: 'pointer' }}> 
		     	<Button variant="contained" color="primary" onClick={() => { getData() }}>
			     	Fetch Schedule 
			   	</Button>
		   	</span>
		   	<div>
		   		{getDataList()}
		   	</div>
	    </div>
  );
};

export default HelloWorld;