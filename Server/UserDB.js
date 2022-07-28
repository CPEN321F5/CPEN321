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
					console.log('[UserDB] Connected to ' + mongoUrl + '/' + dbName);
					resolve(client.db(dbName));
				}
			}
		)
	});
}

//get the profile for a user
//resolve to null if don't exist
Database.prototype.getProfile = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            var query = {UserID : user_id}
            var user_profile = db.collection("Profile").findOne(query)
            resolve(user_profile)
        })
    )
}

//add an user to the database, resolve to insert result when done
Database.prototype.addUser = function(user){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            //add user to database
            console.log("[UserDB] Adding a user")
            var result = db.collection("Profile").insertOne(user)
            resolve(result)
        })
    )
}

//updates the user profile based on the query, return ture if successful
Database.prototype.updateProfile = function(profile){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            if(profile != null && Object.prototype.hasOwnProperty.call(profile, "UserID")){
                console.log("[UserDB] updating user profile for user " + profile.UserID)

                //configuring the parameter for update
                const filter = { UserID: profile.UserID.toString() };
                const options = { upsert: false };
                const update_profile = {$set: profile}

                console.log(update_profile)
                var result = db.collection("Profile").updateOne(filter, update_profile, options)
                resolve(result);
            }
            else{
                console.log("[UserDB] invalid update request ")
                resolve({matchedCount : 0, modifiedCount : 0})
            }
        }).then(result =>{
            console.log("[UserDB] found " + result.matchedCount + "document, updated " + result.modifiedCount + "documents")
            if (result.matchedCount >= 1){
                console.log("[UserDB] successfully updated profile")
                return true
            }else{
                console.log("[UserDB] failed to updated profile")
                return false
            }
        })
    )
}

//remove a user with user_id , resolve to true if successful, false if user does not exist
Database.prototype.removeUser = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            //removing a user
            console.log("[UserDB] Removing a user with id " + user_id)
            var query = {UserID : user_id}
            var result = db.collection("Profile").deleteOne(query)
            resolve(result)
        }).then(result =>{
            console.log("[UserDB] Removed " + result.deletedCount + " Users")
            if(result.deletedCount === 1){
                console.log("[UserDB] Delete Successful userid " + user_id)
                return true
            }
            else{
                console.log("[UserDB] Delete failed, Did not find user " + user_id)
                return false
            }
        })
    )
}


module.exports = Database;