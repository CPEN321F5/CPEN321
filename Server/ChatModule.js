const Chat_Database = require('./ChatDB')

function Chat_module(){
    console.log("initializing Chat Module")
    this.chat_db = new Chat_Database("mongodb://localhost:27017", "F5")
}

//initialize a conversation between two users, 
Chat_module.prototype.initConversation = function(UserID1, UserID2){
    //check if any conversation for these two user exist
    var query = {
        $or: [
            {"user1" : UserID1 , "user2" : UserID2},
            {"user1" : UserID2 , "user2" : UserID1}
        ]
    }
    return this.chat_db.getConversationsByCondition(query).then(conversation => 
        new Promise((resolve, reject) => {
            if(conversation.length > 0){
                console.log("[ChatModule]Found existing conversation between users")
                resolve(conversation[0]) 
            }
            else{
                //no previous conversation between the users
                console.log("[ChatModule]Creating new conversation for users")
                //create new conversation
                conversation = {
                    "user1"          : UserID1.toString(),
                    "user2"          : UserID2.toString(),
                    "messages"       : []
                }
                //adding new conversation to db
                this.chat_db.initConversation(conversation).then(new_conversation => {
                    console.log("[ChatModule] Successfully add conversation")
                    resolve(new_conversation)
                })
            }
        })
    )
}


Chat_module.prototype.getConversationList = function(UserID){
    //query to find conversation that the user is in
    var query = {
        $or: [
            {"user1" : UserID},
            {"user2" : UserID}
        ]
    }
    var projection = {  
        projection:{
            "user1" : 1,
            "user2" : 1,
            "conversationID" : 1,
        }
    }
    console.log("[ChatModule] finding conversation for user " + UserID)
    return this.chat_db.getConversationsByCondition(query, projection).then(conversations => {
        console.log(conversations)
    })
}


var cm = new Chat_module()
// cm.initConversation("2", "4").then(result => console.log(result))
cm.getConversationList("1")