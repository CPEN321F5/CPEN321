const Authentication = require("../../Authentication")

// jest.mock('../Authentication')

//setting up the test object
const am = new Authentication()

//mocking getUser response
// const Userresp = {  "UserID"            : "1",
//                     "Email"             : "N/A",
//                     "DisplayName"       : "N/A",
//                     "FirstName"         : "N/A",
//                     "LastName"          : "N/A"}

// am.getUser.mockResolvedValue(Userresp)

// //mocking signIn response
// am.signin.mockResolvedValue(Userresp)

// //mocking update profile
// am.updateProfile.mockResolvedValue(true)

// //mocking removeUser
// am.removeUser.mockResolvedValue(true)


//testing the SignIn interface of authentication module

var default_user = {
    //Basic info
    "UserID"            : "0000001",
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

test('Testing authentication module - SignIn interface - New user login', () => {
    //user id of 0000001 first time log in
    return am.signin("0000001").then(result => expect(result).toEqual(expect.objectContaining(default_user)))
})

test('Testing authentication module - SignIn interface - Existing user login', () => {
    //user id of 0000001 second time signin, profile is unchanged from last time
    return am.signin("0000001").then(result => expect(result).toEqual(expect.objectContaining(default_user)))

})

var non_standard_user = {
    //Basic info
    "UserID"            : "a21*2@#Y!#(@",
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

test('Testing authentication module - SignIn interface - non standard userID', () => {
    //user id of “a21*2@#Y!#(@”
    return am.signin('a21*2@#Y!#(@').then(result => expect(result).toEqual(expect.objectContaining(non_standard_user)))

})

test('Testing authentication module - SignIn interface - null input', () => {

    return am.signin().then(result => expect(result).toStrictEqual({}))

})


//testing the updateProfile interface of authentication module

var default_user_update = {
    //Basic info
    "UserID"            : "0000001",
    "Email"             : "a@gov.co.uk",
    "Status"            : "normal"
}

test('Testing authentication module - updateProfile interface - Successful profile update', () => {
    //valid update expect to be true
    return am.updateProfile(default_user_update).then(result => expect(result).toBeTruthy())
})

test('Testing authentication module - updateProfile interface - userID only, no real update', () => {
    //same update angin, should result in no change
    return am.updateProfile(default_user_update).then(result => expect(result).toBeTruthy())

})

test('Testing authentication module - updateProfile interface - no userID in update request', () => {
    var invalid_profile_update = {
    //Basic info
    "Email"             : "a@gov.co.uk",
    "Status"            : "normal"
}
    //no user ID, should result in false
    return am.updateProfile(invalid_profile_update).then(result => expect(result).toBeFalsy())

})

test('Testing authentication module - updateProfile interface - adding new field in profile', () => {

    var new_field_update = {
        //Basic info
        "UserID"            : "0000001",
        "Email"             : "a@gov.co.uk",
        "Status"            : "normal",
        "newField"          : "this is a field that did not exist in the profile"
    }

    return am.updateProfile(new_field_update).then(result => expect(result).toBeTruthy())

})

test('Testing authentication module - updateProfile interface - null input ', () => {
    return am.updateProfile().then(result => expect(result).toBeFalsy())

})

test('Testing authentication module - updateProfile interface - non existing user', () => {

    var non_existant_update = {
        //Basic info
        "UserID"            : "0000002",
        "Email"             : "a@gov.co.uk",
        "Status"            : "normal",
        "newField"          : "this is a field that did not exist in the profile"
    }

    return am.updateProfile(non_existant_update).then(result => expect(result).toBeFalsy())

})



//testing the getUser interface of authentication module

var existing_user = {
    UserID: '0000001',
    Email: 'a@gov.co.uk',
    DisplayName: 'N/A',
    FirstName: 'N/A',
    LastName: 'N/A',
    Unit: 'N/A',
    Address_line_1: 'N/A',
    Address_line_2: 'N/A',
    City: 'N/A',
    Province: 'N/A',
    Country: 'N/A',
    ZIP_code: 'N/A',
    Phone: 'N/A',
    balance: '0',
    Status: 'normal',
  }

test('Testing authentication module - getUser interface - getting profile for a existing user', () => {

    return am.getUser("0000001").then(result => expect(result).toEqual(expect.objectContaining(existing_user)))

})

test('Testing authentication module - getUser interface - getting profile for a non existing user', () => {

    return am.getUser("000002").then(result => expect(result).toBeNull())

})

test('Testing authentication module - getUser interface - null user id', () => {

    return am.getUser().then(result => expect(result).toBeNull())

})


//testing the removeUser interface of authentication module

test('Testing authentication module - removeUser interface - removing a existing user ', () => {

    return am.removeUser("0000001").then(result => expect(result).toBeTruthy())

})

test('Testing authentication module - removeUser interface - removing another existing user ', () => {

    return am.removeUser('a21*2@#Y!#(@').then(result => expect(result).toBeTruthy())

})

test('Testing authentication module - removeUser interface - removing a non existing user', () => {

    return am.removeUser("0000002").then(result => expect(result).toBeFalsy())

})

test('Testing authentication module - removeUser interface - null user id ', () => {

    return am.removeUser().then(result => expect(result).toBeFalsy())

})
