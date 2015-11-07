# redirector
Sample App to handle HTTP POST Redirect from Android and IOS 

Web Server - A simple express server redirecting GET and POST requests to a different end point. Build using 
[angular-fullstack-generator] (https://github.com/DaftMonk/generator-angular-fullstack). The front end code is not used. 

Android - Uses [OKHttp](https://github.com/square/okhttp) and [Retrofit 2.0+](https://github.com/square/retrofit). The POST redirect returns 
the Redirect URL and a new request needs to be made by the client.
