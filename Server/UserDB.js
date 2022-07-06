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
					console.log('[MongoClient] Connected to ' + mongoUrl + '/' + dbName);
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

Database.prototype.getProfile = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            resolve({"id" : "test_id"})
        })
    )
}

Database.prototype.userExist = function(user_id){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            resolve(true)
        })
    )
}

Database.prototype.addUser = function(user){
    return this.connected.then(
        db => new Promise((resolve, reject) => {
            //add user to database
            resolve(true)
        })
    )
}



module.exports = Database;