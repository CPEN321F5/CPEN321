const User_Database = require('./UserDB.js')

//UserDB


function Auth_module(){
    console.log("initializing Auth Module")
    this.user_db = new User_Database("mongodb://localhost:27017", "user")
} 

//take user id for signin, if user is new, resolves to indicating the user is new
//resolve to the user profile found in UserDB otherwise.
Auth_module.prototype.signin = function(user_id){
    return this.user_db.userExist(user_id).then((user_exist) => 
        new Promise((resolve, reject) => {
            if(user_exist){
                //not a new user, resolving to the user profile
                console.log("found user with user_id" + user_id)
                resolve(this.user_db.getProfile(user_id))
            }
            else{
                //a new user, adding to the data base
                default_user = {
                    //Basic info
                    "UserID"            : user_id,
                    "Email"             : "N/A",
                    "DisplayName"       : "N/A",
                    "FirstName"         : "N/A",
                    "LastName"          : "N/A",
                    //Address
                    "Unit"              : "N/A",
                    "Address_line_1"    : "N/A",
                    "Address_line_2"    : "N/A",
                    "City"              : "N/A",
                    "Province"          : "N/A",
                    "Country"           : "N/A",
                    "ZIP_code"          : "N/A",
                    "Phone"             : "N/A",
                    
                    "Status"            : "new_user"
                }
                this.user_db.addUser(default_user)
                resolve({"status" : "new_user"})
            }
        })
    )
}

//returns result from userDB
Auth_module.prototype.updateProfile = function(query){
    return this.user_db.updateProfile(query)
}

am = new Auth_module()

am.signin(123).then(result => {console.log(result)})