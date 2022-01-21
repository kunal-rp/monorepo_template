import Head from 'next/head'
import Login from '../components/Login'

import { useState,useEffect} from 'react';


export default function Home(props) {


  return (
    <div className="p-5 space-y-3">
      <Head>
        <title>Next Frapp</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <div className=""> 
        Index
        <Login />
       </div> 
    </div>
  )
}

