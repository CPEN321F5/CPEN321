const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await request.post("/chat/initconversation/7/1")
    await request.post("/chat/initconversation/7/2")
    await request.post("/chat/initconversation/7/3")
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Get list of chat a user is involved in - user with chat", async () => {
    var response = await request.get("/chat/getconversationlist/7")
    expect(response.status).toBe(200)
    expect(response.body.length).toBeGreaterThanOrEqual(3)
})


test("Get list of chat a user is involved in - user without chat", async () => {
    var response = await request.get("/chat/getconversationlist/100")
    expect(response.status).toBe(200)
    expect(response.body.length).toBe(0)
})
