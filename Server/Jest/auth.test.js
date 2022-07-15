const Authentication = require("../Authentication")

jest.mock('../Authentication')

//setting up the mock object
const am = new Authentication()

//mocking getUser response
const Userresp = {  "UserID"            : "1",
                    "Email"             : "N/A",
                    "DisplayName"       : "N/A",
                    "FirstName"         : "N/A",
                    "LastName"          : "N/A"}

am.getUser.mockResolvedValue(Userresp)

//mocking signIn response
am.signin.mockResolvedValue(Userresp)

//mocking update profile
am.updateProfile.mockResolvedValue(true)

//mocking removeUser
am.removeUser.mockResolvedValue(true)




test('Testing authentication module', () => {
    
    am.signin(111).then(result => expect(result).toBe(Userresp))

    am.getUser(111).then(result => expect(result).toBe(Userresp))

    am.updateProfile().then(result => expect(result).toBe(true))

    am.removeUser().then(result => expect(result).toBe(true))

})
