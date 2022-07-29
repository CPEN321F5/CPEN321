const app = require('../server')
const supertest = require('supertest')
const Authentication = require("../Authentication")
const request = supertest(app)

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
    await request.post("/user/signin/2/")
})

//signing in for a existing user
test("Get User Profile - get profile for exsting user", async () => {
    var response = await request.get("/user/getprofile/2/")
    expect(response.status).toBe(200)
    expect(response.body.UserID).toBeDefined()
})


test("Get User Profile - get profile for non existing user", async () => {
    //user now exist as the first test would have created the use
    var response = await request.get("/user/getprofile/definetly_not_a_user/")
    expect(response.status).toBe(404)
})

test("Get User Profile - get profile with no id", async () => {
    //user now exist as the first test would have created the use
    var response = await request.get("/user/getprofile/")
    expect(response.status).toBe(404)
})

afterAll(async () => {
    const am = new Authentication()
    await am.removeUser("2")
})