const app = require('../server')
const supertest = require('supertest')
const Authentication = require("../Authentication")
const request = supertest(app)

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
    await request.post("/user/signin/4/")
})

test("Updating User Wallet - update balance for exsting user", async () => {
    var response = await request.put("/user/updateprofile/").send({UserID: '4' , Balance : "500"}).set('Accept', 'application/json')
    expect(response.status).toBe(200)
    response = await request.get("/user/getprofile/4/")
    expect(response.body.Balance).toBe("500")
})


test("Updating User Wallet - update balance for non exsting user", async () => {
    var response = await request.put("/user/updateprofile/").send({UserID: 'not a userID' , FirstName : "Tom"}).set('Accept', 'application/json')
    expect(response.status).toBe(404)
})

afterAll(async () => {
    const am = new Authentication()
    await am.removeUser("4")
})
