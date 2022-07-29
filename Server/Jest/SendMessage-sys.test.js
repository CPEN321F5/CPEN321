const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
const ChatModule = require("../ChatModule")
const cm = new ChatModule()

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})
var conversationID

test("Send message - send message object", async () => {
    var response = await request.post("/chat/initconversation/19/20")
    expect(response.status).toBe(200)
    conversationID = response.body.conversationID
    await cm.addMessage({
        conversationID: conversationID,
        message: "好咯吼吼吼",
        userID: "5",
        time: Date.now().toString()
    })   
    
    var response = await request.post("/chat/initconversation/19/20")
    expect(response.status).toBe(200)
    expect(response.body.messages.length).toBeGreaterThanOrEqual(1)
})
