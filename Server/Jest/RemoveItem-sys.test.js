const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)

var item_id
beforeAll(async () => {
    var response = await request.post("/item/postitem/").send({name: 'Tank' , sellerID : "1", Price : "10"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Remove Item - deleting a exisitng item", async () => {
    var response = await request.delete("/item/removeitem/" + item_id)
    expect(response.status).toBe(200)
})


test("Remove Item - deleting a non-exisitng item", async () => {
    //user now exist as the first test would have created the use
    var response = await request.delete("/item/removeitem/" + "not a id")
    expect(response.status).toBe(404)
})
