const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")

beforeAll(async () => {
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Post Item - post a normal item", async () => {
    var response = await request.post("/item/postitem/").send({name: 'Table' , Seller : "1"}).set('Accept', 'application/json')
    expect(response.status).toBe(200)
    expect(response.body.ItemID).toBeDefined()
    item_id = response.body.ItemID
})


test("Post Item - post invalid item", async () => {
    var response = await request.post("/item/postitem/").send({Description: 'Noname'}).set('Accept', 'application/json')
    expect(response.status).toBe(400)
})

afterAll(async () => {
    const im = new ItemModule()
    await im.removeItem(item_id)
})
