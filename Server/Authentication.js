const User_Database = require('./UserDB.js')

//UserDB


function Auth_module(){
    console.log("initializing Auth Module")
    this.user_db = new User_Database("mongodb://localhost:27017", "user")
} 

Auth_module.prototype.signin = function(user_id){
    return this.user_db.userExist(user_id).then((user_exist) => 
        new Promise((resolve, reject) => {
            if(user_exist){
                console.log("found user with user_id" + user_id)
            }
            resolve("A")
        })
    )
}


am = new Auth_module()

am.signin(123).then(result => {console.log(result)})