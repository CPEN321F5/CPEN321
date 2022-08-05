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
                this.chat_db.getUserName(UserID1.toString()).then(name1 => {
                    this.chat_db.getUserName(UserID2.toString()).then(name2 => {
                        conversation = {
                            "user1"          : UserID1.toString(),
                            "user2"          : UserID2.toString(),
                            "user1name"      : name1,
                            "user2name"      : name2,
                            "messages"       : []
                        }
                        //adding new conversation to db
                        this.chat_db.initConversation(conversation).then(new_conversation => {
                            console.log("[ChatModule] Successfully add conversation")
                            resolve(new_conversation)
                        })
                    })
                })
                
            }
        })
    )
}


//get a list of conversation with import information in the following format
// {
//     "conversationID" : "0000",
//     "name" : "Alice",
//     "Last message" : "this is a message",
//     "Time"          : "July 10, 10:10" 
// },

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
    return new Promise((resolve, reject) => {
        this.chat_db.getConversationsByCondition(query, projection).then(conversations => {
            chatlist = []
            loop_count = conversations.length
            //using a promise to wait for all async code in for each to finish
            var conversation_promist = new Promise((resolve, reject) => {
                //no message for user, return empty list
                if(loop_count === 0){
                    resolve([])
                }
                conversations.forEach((conversation, index, array) => {
                    // console.log(conversation)
                    //getting the converssation id of the conversation
                    var chatlistEntry = {}
                    chatlistEntry.conversationID = conversation.conversationID
        
                    //getting the userid and name of the other user
                    var other_user_id
                    if(conversation.user1 == UserID){
                        other_user_id = conversation.user2
                    }
                    else{
                        other_user_id = conversation.user1
                    }
                    //get the username of the id
                    this.chat_db.getUserName(other_user_id).then(other_user_name => {
                        chatlistEntry.name = other_user_name
                        //finished getting user name
                        //fetching the latest messages from db
                        this.chat_db.findNewestMessage(conversation.conversationID, 1).then(messages => {
                            if(messages[0]){
                                chatlistEntry.Last_message = messages[0].message
                                var date = new Date(parseInt(messages[0].time, 10))
                                chatlistEntry.Time = date.toLocaleString()
                            }
                            else{
                                chatlistEntry.Last_message = ""
                                chatlistEntry.Time = ""
                            }
                            
                            //adding the chatlist entry to the chatlist array
                            chatlist.push(chatlistEntry)
                            //using a loop_count to keep track of 
                            loop_count -= 1;
                            console.log(loop_count)
                            if(loop_count === 0){
                                resolve(chatlist)
                            }
                        })
                    })
                })
            })
            conversation_promist.then(list => {
                resolve(list)
            })
        })
    })
}

//get a full conversation object by conversationID
Chat_module.prototype.getConversation = function(conversationID){
    return new Promise((resolve, reject) => {
        this.chat_db.getConversation(conversationID).then(conversation => {
            if(conversation == null){
                resolve()
            }
            else if(Object.prototype.hasOwnProperty.call(conversation, "user1") && Object.prototype.hasOwnProperty.call(conversation, "user2")){
                this.chat_db.getUserName(conversation.user1.toString()).then(name1 => {
                    this.chat_db.getUserName(conversation.user2.toString()).then(name2 => {
                        //got the username for each userid
                        conversation.user1name = name1
                        conversation.user2name = name2
                        resolve(conversation)                        
                    })
                })
            }
            else{
                resolve()
            }
        })
    })
}

//Add message to a conversation
//message need to have conversationID
Chat_module.prototype.addMessage = function(message){
    Object.prototype.hasOwnProperty.call(message, "user1")
    if(!Object.prototype.hasOwnProperty.call(message, "conversationID")){
        console.log("[ChatModule] recieved a homeless message without conversation iD")
    }
    else if(!Object.prototype.hasOwnProperty.call(message, "userID") 
            || !Object.prototype.hasOwnProperty.call(message, "time"))
            {
        console.log("[ChatModule] recieved a invalid message")
    }
    else{
        //unify message datatype
        message.conversationID = message.conversationID.toString()
        message.userID = message.userID.toString()
        message.time = message.time.toString()
        return this.chat_db.addMessageToConversation(message)
    }
}

//remove conversation
Chat_module.prototype.deleteConversation = function(conversationID){
    if(conversationID == null){
        return new Promise((resolve, reject) => {
            resolve(false)
        })
    }
    return this.chat_db.removeConversation(conversationID)
}

module.exports = Chat_module




// var cm = new Chat_module()
// cm.initConversation("1", "4").then(result => console.log(result))
// cm.getConversationList("10").then(list => console.log(list))

//  cm.getConversation(1657442833595).then(conversation => {
//      console.log(conversation)
// })

// cm.chat_db.findNewestMessage(1657434736367, 3).then(messages => console.log(messages))



// sample_message = 
// {
//     "conversationID" : "1657442833595",
//     "userID"    :  "1",
//     "type"      :  "text",
//     "message"   :  "This is a message",
//     "time"      :  Date.now()
// }
// cm.addMessage(sample_message)