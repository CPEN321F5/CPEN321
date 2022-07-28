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
            const query = {$text : { $search : key_word.toString()}}
            //const filter = {timeExpire : { $gt : Date.now() }}
            //TODO add expire

            db.collection("Items").find(query).toArray((err, result) => {
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


module.exports = Database