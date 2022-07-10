const { MongoClient, ObjectID } = require('mongodb');	// require the mongodb driver
const db_coll = "Chat" 

//connect to the new database for chat module
function ChatDB(mongoUrl, dbName){
	if (!(this instanceof ChatDB)) return new ChatDB(mongoUrl, dbName);
	this.connected = new Promise((resolve, reject) => {
		MongoClient.connect(
			mongoUrl,
			{
				useNewUrlParser: true
			},
			(err, client) => {
				if (err) reject(err);
				else {
					console.log('[ItemDB] Connected to ' + mongoUrl + '/' + dbName);
					resolve(client.db(dbName));
				}
			}
		)
	});
	this.status = () => this.connected.then(
		db => ({ error: null, url: mongoUrl, db: dbName }),
		err => ({ error: err })
	);
}


//create an conversation, take a conversation object and insert into chat DB
ChatDB.prototype.initConversation = function(conversation){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			//using Date.now to create a "unique" id for each conversation
			var conversationID = Date.now().toString()
			conversation.conversationID = conversationID
			console.log("[ChatDB] Initiating a conversation")
			db.collection(db_coll).insertOne(conversation)
			resolve(conversation)
		})
	)
}

//get an conversation object
ChatDB.prototype.getConversation = function(conversationID){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			var query = {conversationID : conversationID.toString()}
			console.log("[ChatDB] Getting a conversation by ID " + conversationID)
            var conversation = db.collection(db_coll).findOne(query)
            resolve(conversation)
		})
	)
}

//get a list of conversation object by query
//this is used to get conversations by user name or other conditions
ChatDB.prototype.getConversationsByCondition = function(query, projection){
	console.log("[ChatDB] Getting a conversation by condition")
	console.log(query)
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			if(projection){
				db.collection(db_coll).find(query, projection).toArray((err, result) => {
					if(err){
						reject(err)
					}
					resolve(result)
				})
			}
			else{
				db.collection(db_coll).find(query).toArray((err, result) => {
					if(err){
						reject(err)
					}
					resolve(result)
				})
			}
			
		})
	)
}

//Add a message to the message array of a conversation
//message need to have conversationID
ChatDB.prototype.addMessageToConversation = function(message){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			console.log("[ChatDB] Adding a message to conversation")
			var conversationID = message.conversationID

			//configuring update
			const filter = {conversationID : conversationID.toString()}
			const message_update = { $push: { messages : message } }

			console.log(filter)
			var result = db.collection(db_coll).updateOne(filter, message_update)
			resolve(result)
		}).then(result =>{
            console.log("[ChatDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.modifiedCount >= 1){
                return true
            }else{
                console.log("[ChatDB] failed to insert message")
                return false
            }
        })
	)
}


//interface with the user collection to get the display username of a user
ChatDB.prototype.getUserName = function(UserID){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			console.log("[ChatDB] getting user name of " + UserID)
			var query = {UserID : UserID}
            var user_profile = db.collection("Profile").findOne(query).then(user_profile =>{
				if(user_profile != null && user_profile.hasOwnProperty("DisplayName")){
					resolve(user_profile.DisplayName)
				}
				else{
					resolve("NoName")
				}
			})	
		})
	)
}

//use aggregation pipeline to fetch num_messages message by latest time
//resolve to a array of message objects
ChatDB.prototype.findNewestMessage = function(ConversationID, num_messages){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			var aggcur = db.collection(db_coll).aggregate([
				{$match:{"conversationID" : ConversationID.toString()}},
				{$unwind:"$messages"},
				{$sort:{"messages.time":-1}},
				{$limit:num_messages}
			]);
			var messages = []
			aggcur.forEach(msg => {
				messages.push(msg.messages)
			}).then(
				a => {
					resolve(messages)
				}
			)
		})
	)
}

module.exports = ChatDB
