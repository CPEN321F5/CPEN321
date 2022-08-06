const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")

beforeAll(async () => {
    var response = await request.post("/item/postitem/").send({name: 'Boxer' , Seller : "1", Price : "10", catagory : "desk"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    await request.post("/user/signin/2")
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Get Item - get an existing item by id", async () => {
    var response = await request.get("/item/getbyid/" + item_id)
    expect(response.status).toBe(200)
    expect(response.body.Price).toBe("10")
})


test("Get Item - no item ID", async () => {
    var response = await request.get("/item/getbyid/")
    expect(response.status).toBe(404)
})

test("Get Item with history - get an existing item by id", async () => {
    var response = await request.get("/item/getbyid_history/" + item_id + '/2')
    expect(response.status).toBe(200)
    expect(response.body.Price).toBe("10")
})

test("Get Item with history - get an non-existing item by id", async () => {
    var response = await request.get("/item/getbyid_history/" + "notitemID" + '/1')
    expect(response.status).toBe(200)
})

test("Get Item with history - no item ID", async () => {
    var response = await request.get("/item/getbyid_history/")
    expect(response.status).toBe(404)
})

afterAll(async () => {
    const im = new ItemModule()
    await im.removeItem(item_id)
})
