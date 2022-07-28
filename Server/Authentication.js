const User_Database = require('./UserDB.js')

//UserDB


function Auth_module(){
    console.log("initializing Auth Module")
    this.user_db = new User_Database("mongodb://localhost:27017", "F5")
} 

//take user id for signin, if user is new, resolves to new user profile
//resolve to the user profile found in UserDB otherwise.
//the status in the user profile indicate user status, if status is new user, front end should prompt to update profile
Auth_module.prototype.signin = function(user_id){
    if(user_id == null){
        console.log("[Auth] invalid signin request " + user_id)
        return new Promise((resolve, reject) => {
            resolve ({})
        })
    }
    console.log("[Auth] Signin Request for " + user_id)
    return this.user_db.getProfile(user_id.toString()).then((user_profile) => 
        new Promise((resolve, reject) => {
            if(user_profile){
                //not a new user, resolving to the user profile
                console.log("[Auth] found user with user_id " + user_id.toString())
                resolve(user_profile)
            }
            else{
                //user_profile is null, a new user, adding to the data base
                console.log("[Auth] no profile for user id " + user_id)
                var default_user = {
                    //Basic info
                    "UserID"            : user_id.toString(),
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

                    "balance"           : "0",
                    
                    "Status"            : "new_user"
                }
                this.user_db.addUser(default_user).then(result => {
                    console.log("[Auth] Successfully add user, insert id " + result.insertedId)
                    resolve(default_user)
                })
            }
        })
    )
}

//returns result from userDB
Auth_module.prototype.updateProfile = function(profile){
    return this.user_db.updateProfile(profile)
}

Auth_module.prototype.removeUser = function(user_id){
    return this.user_db.removeUser(user_id)
}

Auth_module.prototype.getUser = function(user_id){
    return this.user_db.getProfile(user_id)
}


module.exports = Auth_module
//testing code

// am = new Auth_module()

// am.signin(2).then(result => {console.log(result)})
// // am.removeUser(1234).then(result => {console.log(result)})
// profile_user = {
//     //Basic info
//     "UserID"            : 1234,
//     "Email"             : "exampleemail",
//     "DisplayName"       : "N/A",
//     "FirstName"         : "N/A",
//     "LastName"          : "N/A",
//     //Address
//     "Unit"              : "N/A",
//     "Address_line_1"    : "N/A",
//     "Address_line_2"    : "N/A",
//     "City"              : "N/A",
//     "Province"          : "N/A",
//     "Country"           : "N/A",
//     "ZIP_code"          : "N/A",
//     "Phone"             : "N/A",
    
//     "Status"            : "normal"
// }
// //am.updateProfile(profile_user)