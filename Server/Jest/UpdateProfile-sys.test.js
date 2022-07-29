const app = require('../server')
const supertest = require('supertest')
const Authentication = require("../Authentication")
const request = supertest(app)

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
    await request.post("/user/signin/3/")
})

test("Updating User Profile - update profile for exsting user", async () => {
    var response = await request.put("/user/updateprofile/").send({UserID: '3' , FirstName : "Tom"}).set('Accept', 'application/json')
    expect(response.status).toBe(200)
})


test("Updating User Profile - update profile for non exsting user", async () => {
    var response = await request.get("/user/updateprofile/").send({UserID: 'not a userID' , FirstName : "Tom"}).set('Accept', 'application/json')
    expect(response.status).toBe(404)
})

test("Updating User Profile - update profile with no UserID", async () => {
    var response = await request.get("/user/updateprofile/").send({FirstName : "Tom"}).set('Accept', 'application/json')
    expect(response.status).toBe(404)
})

afterAll(async () => {
    const am = new Authentication()
    await am.removeUser("3")
})