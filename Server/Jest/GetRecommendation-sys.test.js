const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)

beforeAll(async () => {
    await request.post("/user/signin/15")
    await request.put("/user/updateprofile/").send({UserID: '15' , FirstName : "Tom", category_history : ["books", "pets"]}).set('Accept', 'application/json')
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Get recommendation - get recommendation for active user", async () => {
    var response = await request.get("/recommand/getrecommendation/15")
    expect(response.status).toBe(200)
})

test("Get recommendation - get recommendation for non-existing user", async () => {
    var response = await request.get("/recommand/getrecommendation/999")
    expect(response.status).toBe(200)
})


