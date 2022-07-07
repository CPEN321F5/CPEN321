var express = require("express")
var bodyParser = require("body-parser")
var app = express()
app.use(express.json())
app.use(express.urlencoded({ extended: true })) // to parse application/x-www-form-urlencoded

var http = require('http');
var ipaddr = "8.8.8.8"



//User Authentication Module
const User_Authentication = require("./Authentication")
auth_module = new User_Authentication()

///////////////////User authentication Listners//////////////////////////
//post request for signing in, creats a new profile if it is a new signin
app.post("/user/signin/:user_id", (req,res) => {
    console.log("[Server] login request from user " + req.params.user_id)
    if(!req.params.user_id){
        //invalid request
        res.status(400).send("invalid request: login user id not specified")
    }
    else{
        //login successful send user profile or a default profile for new user
        auth_module.signin(req.params.user_id).then((profile) => {
            res.send(profile)
        })
    }
})

//put request for updating profile for a user
app.put("/user/updateprofile/", (req,res) => {
    var profile = req.body
    if(profile == null || !profile.hasOwnProperty("UserID")){
        //invalid request
        res.status(400).send("invalid request: no user id in profile")
    }
    else{
        //convert UserID to string for uniformity
        profile.UserID = profile.UserID.toString()
        console.log("[Server] Updating profile for user " + req.body.UserID)
        auth_module.updateProfile(profile).then(result =>{
            if(result){
                res.status(200).send("successfully updated profile")
            }
            else{
                res.status(200).send("nothing changed or profile does not exist, so i created it")
            }
        })
    }
})

//get user profile
app.get("/user/getprofile/:user_id", (req,res) => {
    console.log("[Server] getting profile for user " + req.params.user_id)
    if(!req.params.user_id){
        //invalid request
        res.status(400).send("invalid request: user id not specified")
    }
    else{
        //login successful send user profile or a default profile for new user
        auth_module.getUser(req.params.user_id).then((profile) => {
            res.send(profile)
        })
    }
})


app.get("/", (req,res) =>{
    res.send("DATA")
})

app.get("/img", (req,res) =>{
    res.sendFile("/home/CPEN321F5/F5/img1.png");
})



async function run(){
    try{
        var server = app.listen(8081, (req,res) => {
            var host = server.address().address
            var port = server.address().port
            console.log("Example server at $s : $s", host, port);
        })
    }
    catch(err){
        console.log(err)
    }
}



http.get({'host': 'api.ipify.org', 'port': 80, 'path': '/'}, function(resp) {
  resp.on('data', function(ip) {
    ipaddr = ip.toString()
    console.log(ipaddr)
    console.log("My public IP address is: " + ipaddr);
  });
});


run()