const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")
const im = new ItemModule()

beforeAll(async () => {
    im.item_db.createIndex()
    var response = await request.post("/item/postitem/").send({name: 'RocketShip' , Seller : "1", catagory : "Furniture"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Search by Catagory - search by a catagory", async () => {
    var response = await request.get("/item/getbycond/catagory/Furniture")
    expect(response.status).toBe(200)
    expect(response.body[0].name).toBe("RocketShip")
})

afterAll(async () => {
    await im.removeItem(item_id)
})
