var express = require("express")
var app = express()
app.use(express.json())
app.use(express.urlencoded({ extended: true })) // to parse application/x-www-form-urlencoded

var http = require('http');
var ipaddr = "8.8.8.8"


//Google ID token authentication
const {OAuth2Client} = require('google-auth-library');
const CLIENT_ID = "1073711084344-vvjarmtoahi6mqjur4dglgnocjfm8j4i.apps.googleusercontent.com"
const client = new OAuth2Client(CLIENT_ID);

async function verify(token) {
  try{
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: CLIENT_ID,
    });
    const payload = ticket.getPayload();
    const userid = payload['sub'];
    console.log("varified user with userid " + userid)
    // If request specified a G Suite domain:
    // const domain = payload['hd'];
    return true
  }
  catch(err){
    console.log(err)
    return false
  }
}

/////////////////////////////////////////User Authentication Module/////////////////////////////

const User_Authentication = require("./Authentication")
var auth_module = new User_Authentication()

////////////////////////////////////////User authentication Listners///////////////////////////////////////////

//post request for signing in, creats a new profile if it is a new signin
app.post("/user/signin/:user_id", (req,res) => {
    console.log("[Server] login request from user " + req.params.user_id + "with Token" + req.body.Token)
    
    if(!req.params.user_id || !verify(req.body.Token)){
        //invalid request
        res.status(400).send("invalid login request")
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
    if(profile == null || !Object.prototype.hasOwnProperty.call(profile,"UserID")){
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
                res.status(404).send("no matching profile")
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
            if(profile == null){
                res.status(404).send("user not found")
            }
            else{
                res.send(profile)
            }
        })
    }
})


/////////////////////////////////////////Item Module/////////////////////////////////////////

const Item_Module = require("./ItemModule")
var item_module = new Item_Module()

//setup routine for updating expired item
setInterval(() => {
    item_module.updateExpireStatus()
}, 60000)

//posting an item
app.post("/item/postitem/", (req,res) => {
    console.log("[Server] posting an item")
    var item = req.body
    if(item == null || !Object.prototype.hasOwnProperty.call(item, "name")){
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
    if(item_update == null || !Object.prototype.hasOwnProperty.call(item_update, "ItemID")){
        //update invalid, need to have itemID
        res.status(400).send("invalid update, need to have itemID")
    }
    else{
        item_module.updateItem(item_update).then(result => {
            if(result){
                res.send("success")
            }
            else{
                res.status(404).send("Item not found")
            }
            
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
    else if(Object.prototype.hasOwnProperty.call(req.body, "UserID")){
        //UserID included, saving item into user's history
        console.log("[Server] saving item history to UserID " + req.body.UserID)
        item_module.getItemByID_history(req.params.item_id, req.body.UserID).then(item => {
            res.send(item)
        })
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
    else if (req.params.type == "admin"){
        console.log("[Server] getting items that need admin attention" + req.params.data)
        item_module.getItemByAdmin(req.params.data).then(items => {
            res.send(items)
        })
    }
    else if (req.params.type == "refund"){
        console.log("[Server] getting items that is refunded" + req.params.data)
        item_module.getItemByRefund(req.params.data).then(items => {
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

app.delete("/item/removeitem/:item_id", (req,res) => {
    console.log("[Server] request for removing item")
    item_id = req.params.item_id
    if(!item_id){
        //update invalid, need to have itemID
        res.status(400).send("invalid delete, need to have itemID")
    }
    else{
        item_module.removeItem(item_id).then(result => {
            if(result){
                res.sendStatus(200)
            }
            else{
                res.sendStatus(404)
            }
            
        })
    }
})



/////////////////////////////////////////Chat Module/////////////////////////////////////////
const SocketServer = require('websocket').server
var Chat_Module = require("./ChatModule");
chat_module = new Chat_Module()

//initializing the socket server
const server = http.createServer((req, res) => { })
//for jest testing un comment when deploying
// server.listen(8080, () => {
//     console.log("Listening on port 8080...")
// })

//configuring the socket server for messaging
wsServer = new SocketServer({ httpServer: server })
const connections = []
wsServer.on('request', (req) => {
    var ConversationID = req.resourceURL.query.ConversationID
    var connection = req.accept()
    //saving the connection conversationID to each conversation
    connection.ConversationID = ConversationID
    console.log('new connection from conversation ' + ConversationID)
    connections.push(connection)
    

    connection.on('message', (message) => {
        var jsonmsg = JSON.parse(message.utf8Data)
        //mark the time the message was recieved
        jsonmsg.time = Date.now().toString()
        if(jsonmsg.send_time != null){
            var timediff = Date.now() - parseInt(jsonmsg.send_time, 10)
            console.log("recieved message with sending time " + timediff)
        }

        connections.forEach(element => {
            //relay the message to the other user connected with same ConversationID
            if (element != connection && element.ConversationID == connection.ConversationID)
                element.sendUTF(JSON.stringify(jsonmsg))
        })
        console.log("[Server] new message")
        chat_module.addMessage(jsonmsg)
    })

    connection.on('close', (resCode, des) => {
        console.log('connection closed')
        connections.splice(connections.indexOf(connection), 1)
    })
}) 

//configuring the http server for Chat Module
app.post("/chat/initconversation/:user1/:user2", (req, res) =>{
    console.log("[Server] request for starting conversation between" + req.params.user1 + "and" + req.params.user2)
    if(!req.params.user1 || !req.params.user2){
        //invalid request
        res.status(400).send("invalid request: user id not specified")
    }
    else{
        chat_module.initConversation(req.params.user1, req.params.user2).then(conversation => {
            //replying with the newly generated conversation
            //of the existing conversation item if found in db
            res.send(conversation)
        })
    }
})

//requesting for a list of conversation involved with a userID
app.get("/chat/getconversationlist/:userID", (req, res) =>{
    console.log("[Server] request for list of conversation for" + req.params.userID)
    if(!req.params.userID){
        //invalid request
        res.status(400).send("invalid request: user id not specified")
    }
    else{
        chat_module.getConversationList(req.params.userID).then(conversationList => {
            //replying with the a list of abbriviated conversation objects
            res.send(conversationList)
        })
    }
})


//requesting for a full conversation object by conversation ID
app.get("/chat/getconversation/:conversationID", (req, res) =>{
    console.log("[Server] request for list of conversation for" + req.params.conversationID)
    if(!req.params.conversationID){
        //invalid request
        res.status(400).send("invalid request: user id not specified")
    }
    else{
        chat_module.getConversation(req.params.conversationID).then(conversation => {
            //replying with the newly generated conversation
            //of the existing conversation item if found in db
            if(conversation == null){
                res.status(404).send("no conversation found")
            }
            else{
                res.send(conversation)
            }
        })
    }
})

// app.get("/img", (req,res) =>{
//     res.sendFile("/home/CPEN321F5/F5/img1.png");
// })



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

module.exports = app

// run() 
///////////for jest testing un comment when deploying