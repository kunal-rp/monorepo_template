import {useEffect} from 'react'
import { useSession, signIn } from "next-auth/react"
import { useRouter } from 'next/router'
import {getUrl} from '../util/protoHelper'

export default function Login(){

	const router = useRouter()
	const  {data: session, status } = useSession()

 	if (status === "authenticated"){
	    return (
	      <>
	        <img src={session.user.picture} />
	        Signed in as {session.user.email} <br />
	        <button onClick={() => router.push('/frapp')}>Open App</button>
	      </>
	    )
	}
	return (
	    <>
	      Not signed in <br />
	      <button onClick={() => signIn()}>Sign in</button>
	    </>
	)

}