const { MongoClient } = require('mongodb');	// require the mongodb driver

//connect to a new database
function Database(mongoUrl, dbName){
	if (!(this instanceof Database)) return new Database(mongoUrl, dbName);
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

//Post Item
//add the item into the database and returns a unique ID for the item
Database.prototype.postItem = function(item){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            var item_id = Date.now()
            item.ItemID = item_id.toString()
            console.log("[ItemDB] Adding an item to DB")
            db.collection("Items").insertOne(item)
            resolve(item)
        })
    )
}

//updates the item based on the query, return ture if successful
//item need to have field itemID
Database.prototype.updateItem = function(item){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            if(item != null && Object.prototype.hasOwnProperty.call(item, "ItemID")){
                console.log("[ItemDB] updating item" + item.ItemID)

                //configuring the parameter for update
                const filter = { ItemID: item.ItemID.toString() };
                const options = { upsert: false };
                const update_profile = {$set: item}

                console.log(update_profile)
                var result = db.collection("Items").updateOne(filter, update_profile, options)
                resolve(result);
            }else{
                console.log("[ItemDB] invalid update request ")
                resolve({matchedCount : 0, modifiedCount : 0})
            }
            

        }).then(result =>{
            console.log("[ItemDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.matchedCount >= 1){
                console.log("[ItemDB] successfully updated item")
                return true
            }else{
                console.log("[ItemDB] failed to updated item, inserted as new profile")
                return false
            }
        })
    )
}

//get an item by its item id
//resolve to null if don't exist
Database.prototype.getItemById = function(item_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            var query = {ItemID : item_id}
            var item = db.collection("Items").findOne(query)
            resolve(item)
        })
    )
}

//get items by condition
Database.prototype.getItemByCondition = function(query){
    console.log("[ItemDB] getting items with condition")
    console.log(query)
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            db.collection("Items").find(query).toArray((err, result) => {
                if(err){
                    reject(err)
                }
                resolve(result)
            })
        })
    )
}


//Search for item using keyword
//expired item will not appear in the result
Database.prototype.searchItem = function(key_word){
    console.log("[ItemDB] searching for item with keyword " + key_word)
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            const query = {$and: [
                {$text : { $search : key_word.toString()}},
                {expired : "false"}
            ]}
            //const filter = {timeExpire : { $gt : Date.now() }}
            //TODO add expire

            db.collection("Items").find(query).toArray((err, result) => {
                if(err){
                    reject(err)
                }
                resolve(result)
            })
        })
    )
}

Database.prototype.createIndex = function(){
    return this.connected.then(
        db => {
            db.collection("Items").createIndex(
            { 
                name : "text",
                Description : "text" 
            },
            {
                weights: {
                    name: 10,
                    Description: 5
                }
            })
        }
    )
}

Database.prototype.removeItem = function(itemID){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            console.log("[ItemDB] deleting item with id " + itemID)
            var query = {ItemID : itemID}
            var result = db.collection("Items").deleteOne(query)
            resolve(result)
        }).then(result =>{
            console.log("[ItemDB] deleted " + result.deletedCount + "document")
            if (result.deletedCount >= 1){
                console.log("[ItemDB] successfully delete item")
                return true
            }else{
                console.log("[ItemDB] failed to delete item, item did not exist")
                return false
            }
        })
    )
}


Database.prototype.chargeUser = function(userID, charge_amount){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            console.log("[ItemDB] charging user " + userID + " with an amount of " + charge_amount)
            //getting the user's current balance
            db.collection("Profile").findOne({ UserID: userID.toString() }).then(profile =>{
                if(profile == null){
                    resolve({matchedCount : 0, modifiedCount : 0})
                }
                else{
                    var balance = parseInt(profile.balance, 10)
                    console.log("[ItemDB] user current balance : " + balance)
                    balance -= parseInt(charge_amount, 10)

                    var update = {
                        UserID : userID,
                        balance : balance.toString()
                    }
                    
                    //configuring the parameter for update
                    const filter = { UserID: userID.toString() }
                    const profile_update = { $set: update }
                    const options = { upsert: false };

                    console.log(profile_update)
                    var result = db.collection("Profile").updateOne(filter, profile_update, options)
                    resolve(result);
                }
            })
        }).then(result =>{
            console.log("[ItemDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.modifiedCount >= 1){
                console.log("[ItemDB] successfully charged the user")
                return true
            }else{
                console.log("[ItemDB] failed to charged the user")
                return false
            }
        })
    )
}

//save an item into the user's history
Database.prototype.saveHistory = function(userID, item){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            console.log("[ItemDB] saving history of " + item.ItemID + " to user " + userID)
            const filter = { UserID: userID.toString() }
            const profile_update = { $push: { category_history : item.catagory, itemid_history : item.ItemID} }
            const options = { upsert: false };

            console.log(profile_update)
            var result = db.collection("Profile").updateOne(filter, profile_update, options)
            resolve(result);
        }).then(result =>{
            console.log("[ItemDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.modifiedCount >= 1){
                console.log("[ItemDB] successfully saved history")
                return true
            }else{
                console.log("[ItemDB] failed to save history")
                return false
            }
        })
    )
}

//Bid Item
Database.prototype.bidItem = function(Itemid, update){
    return this.connected.then(
        db => new Promise((resolve, reject) => {

            console.log("[ItemDB] bidding item " + Itemid)
            const filter = { ItemID: Itemid }
            const options = { upsert: false };

            console.log(update)
            var result = db.collection("Items").updateOne(filter, update, options)
            resolve(result);

        }).then(result =>{
            console.log("[ItemDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.modifiedCount >= 1){
                console.log("[ItemDB] successfully bid item")
                return true
            }else{
                console.log("[ItemDB] failed to bid item")
                return false
            }
        })
    )
}



//sample a number of items within a match condition
Database.prototype.matchItems = async function(Size, Catagory){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            console.log("[ItemDB] finding " + Size + " items with catagory" + Catagory)

            var items = []
            db.collection("Items").aggregate([
                {$match: {$and:[{catagory: Catagory}, {expired : "false"}]}}, // filter the results
                {$sample: {size: Size}} // You want to get 5 docs
            ]).forEach(item => {
                items.push(item)
            }).then(a => {
                resolve(items)
            })
            
        })
    )
}


//interface with the user collection to get the display username of a user
Database.prototype.getUserName = function(UserID){
	return this.connected.then(
		db => new Promise((resolve, reject) => {
			console.log("[ItemDB] getting user name of " + UserID)
			var query = {UserID}
            db.collection("Profile").findOne(query).then(user_profile =>{
				if(user_profile != null && Object.prototype.hasOwnProperty.call(user_profile, "FirstName")){
					resolve(user_profile.FirstName)
				}
				else{
					resolve("NoName")
				}
			})	
		})
	)
}


module.exports = Database