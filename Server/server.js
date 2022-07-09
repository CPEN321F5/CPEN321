var express = require("express")
var bodyParser = require("body-parser")
var app = express()
app.use(express.json())
app.use(express.urlencoded({ extended: true })) // to parse application/x-www-form-urlencoded

var http = require('http');
var ipaddr = "8.8.8.8"



//User Authentication Module
const User_Authentication = require("./Authentication")
var auth_module = new User_Authentication()

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


//Item Module
const Item_Module = require("./ItemModule")
var item_module = new Item_Module

//posting an item
app.post("/item/postitem/", (req,res) => {
    console.log("[Server] posting an item")
    var item = req.body
    if(item == null || !item.hasOwnProperty("name")){
        //item is invalid
        res.status(400).send("Invalid item, Item need to have name")
    }
    else{
        item_module.postItem(item).then(item => {
            //resolves to the updated item with ItemID added
            res.send(item)
        })
    }
})

//updating an item
app.put("/item/updateitem/", (req, res) => {
    console.log("[Server] updating an existing item")
    var item_update = req.body
    if(item_update == null || !item_update.hasOwnProperty("ItemID")){
        //update invalid, need to have itemID
        res.status(400).send("invalid update, need to have itemID")
    }
    else{
        item_module.updateItem(item_update).then(result => {
            res.send(result)
        })
    }
})

//getting an item by id
app.get("/item/getbyid/:item_id", (req, res) => {
    console.log("[Server] getting an item with id " + req.params.item_id)
    if(!req.params.item_id){
        //update invalid, need to have itemID
        res.status(400).send("invalid get, need to have itemID")
    }
    else{
        item_module.getItemByID(req.params.item_id).then(item => {
            res.send(item)
        })
    }
})

//getting a list of item by condition (catagory / buyer / seller)
app.get("/item/getbycond/:type/:data", (req, res) => {
    console.log("[Server] getting items by condition " + req.params.type + req.params.data)
    if(!req.params.type || !req.params.data){
        //invalid argument
        res.status(400).send("invalid arguments")
    }
    else if (req.params.type == "catagory"){
        console.log("[Server] getting items with catagory" + req.params.data)
        item_module.getItemByCatagory(req.params.data).then(items => {
            res.send(items)
        })
    }
    else if (req.params.type == "buyer"){
        console.log("[Server] getting items bought by user" + req.params.data)
        item_module.getItemByBuyer(req.params.data).then(items => {
            res.send(items)
        })
    }
    else if (req.params.type == "seller"){
        console.log("[Server] getting items sold by user " + req.params.data)
        item_module.getItemBySeller(req.params.data).then(items => {
            res.send(items)
        })
    }
    else{
        //invalid condition type
        res.status(200).send("invalid condition type, need to be catagory, buyer, or seller")
    }
})

//searching for an item with an keyword
app.get("/item/search/:keyword", (req,res) => {
    console.log("[Server] searching for items with keyword" + req.params.keyword)
    //an empty keyword is accecpted and will search for all items
    item_module.searchForItem(req.params.keyword).then(items => {
        res.send(items)
    })
})

//initializing the item database with index
app.post("/item/_init_index/", (req,res) => {
    console.log("[Server] Creating Index")
    item_module.item_db.createIndex()
    res.sendStatus(200)
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