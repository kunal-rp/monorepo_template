import NextAuth from "next-auth"
import GoogleProvider from "next-auth/providers/google"
import axios from 'axios';
import Cookies from 'cookies';
import {getUrl, omitAuthCalls} from '../../../util/protoHelper'


const Auth = (req, res) => {
  const cookies = new Cookies(req, res)

  return (
    NextAuth(req, res, {
      site: process.env.NEXTAUTH_URL,
      providers: [
        GoogleProvider({
          clientId: (process.env.GOOGLE_CLIENT_ID ? process.env.GOOGLE_CLIENT_ID : "PH_GOOGLE_CLIENT_ID" ) ,
          clientSecret:(process.env.GOOGLE_CLIENT_SECRET ? process.env.GOOGLE_CLIENT_SECRET : "PH_GOOGLE_CLIENT_SECRET" )
        }),
        // ...add more providers here
      ],
      callbacks: {
        async signIn({ user, account, profile, email, credentials }) {
          console.log('sign in start')
          console.log(getUrl())
          if(!omitAuthCalls()){
            return axios.post(getUrl()+"/gateway/signIn", {"id_token": account.id_token})
              .then(res => { 
                cookies.set("slot_refresh", res.data.refresh_token, {httpOnly: true})
                return true})
              .catch(err => {console.log(err); return false})
          }else{
            return true
          }
        },
         async session({ session, user, token }) {
           console.log("session ")
          console.log(session)
          session.user.picture = token.picture
          return session
        },
        async jwt({ token, user, account, profile, isNewUser }) {
          console.log("jwt ")
          console.log(token)
          return token
        }
      },
      secret : "kV1i6y9v+bWLAcZeqKapTPQGjh6VjgGP8pa8RBvilpQ=",
  }))
}

export default Auth