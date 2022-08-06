const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)

var conversationID
beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    var response = await request.post("/chat/initconversation/20/21")
    conversationID = response.body.conversationID
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Remove Chat - removing existing conversation", async () => {
    var response = await request.delete("/chat/removeconversation/" + conversationID)
    expect(response.status).toBe(200)
})


test("Remove Chat - removing non-existing conversation", async () => {
    //user now exist as the first test would have created the use
    var response = await request.delete("/chat/removeconversation/notaid/")
    expect(response.status).toBe(404)
})
