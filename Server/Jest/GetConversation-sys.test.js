const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
const ChatModule = require("../ChatModule")
const cm = new ChatModule()

var conversationID
beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    var response = await request.post("/chat/initconversation/20/19")
    conversationID = response.body.conversationID
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Get message in a chat room - valid chat id", async () => {
    var response = await request.get("/chat/getconversation/" + conversationID)
    expect(response.status).toBe(200)
})


test("Get message in a chat room - invalid chat id", async () => {
    var response = await request.get("/chat/getconversation/" + "not a id")
    expect(response.status).toBe(404)
})
