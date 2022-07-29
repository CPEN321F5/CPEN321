const app = require('../server')
const supertest = require('supertest')
const Authentication = require("../Authentication")
const request = supertest(app)

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

//signing in for a new user
test("User Sign In - request with new user", async () => {
    var response = await request.post("/user/signin/1/")
    expect(response.status).toBe(200)
    expect(response.body.UserID).toBeDefined()
})


test("User Sign In - request with existing user", async () => {
    //user now exist as the first test would have created the use
    var response = await request.post("/user/signin/1/")
    expect(response.status).toBe(200)
    expect(response.body.UserID).toBeDefined()
})

test("User Sign In - request with no id", async () => {
    //user now exist as the first test would have created the use
    var response = await request.post("/user/signin/")
    expect(response.status).toBe(404)
})

afterAll(async () => {
    const am = new Authentication()
    await am.removeUser("1")
})