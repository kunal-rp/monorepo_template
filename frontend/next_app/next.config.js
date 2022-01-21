
module.exports = {
  distDir: 'nextBuild',
  publicRuntimeConfig : {
    LOCAL_URL : process.env.LOCAL_URL,
    NEXTAUTH_URL: process.env.NEXTAUTH_URL,
    NEXT_PUBLIC_REACT_APP_BASE_URL: process.env.NEXT_PUBLIC_REACT_APP_BASE_URL,
  }
}
