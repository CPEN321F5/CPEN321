const { MongoClient, ObjectID } = require('mongodb');	// require the mongodb driver

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
	this.status = () => this.connected.then(
		db => ({ error: null, url: mongoUrl, db: dbName }),
		err => ({ error: err })
	);
}

//get the profile for a user
Database.prototype.getProfile = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            resolve({"id" : "test_id"})
        })
    )
}

//check if user exist
Database.prototype.userExist = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            resolve(false)
        })
    )
}

//add an user to the database, resolve to true if successful
Database.prototype.addUser = function(user){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            //add user to database
            console.log("[UserDB] Adding a user")
            console.log(user)
            resolve(true)
        })
    )
}

//updates the user profile based on the query, return ture if successful
Database.prototype.updateProfile = function(query){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            console.log("[UserDB] updating user profile")
            resolve(true);
        })
    )
}


module.exports = Database;