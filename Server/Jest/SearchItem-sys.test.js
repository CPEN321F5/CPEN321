const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")
const im = new ItemModule()

beforeAll(async () => {
    im.item_db.createIndex()
    var response = await request.post("/item/postitem/").send({name: 'RocketDime' , Seller : "1", Price : "100", expired : "false"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 1000));
})

test("Search Item - search by name of item", async () => {
    var response = await request.get("/item/search/RocketDime")
    expect(response.status).toBe(200)
})

afterAll(async () => {
    await im.removeItem(item_id)
})
