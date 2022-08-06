const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")
const im = new ItemModule()

beforeAll(async () => {
    im.item_db.createIndex()
    var response = await request.post("/item/postitem/").send({name: 'BattleSh*t' , currentPriceHolder : "10", needAdmin : "true", refund : "true", expired : "true"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Search by Buyer - search by a buyer that have bought items", async () => {
    var response = await request.get("/item/getbycond/buyer/10")
    expect(response.status).toBe(200)
    expect(response.body[0].name).toBe("BattleSh*t")
})

test("Search by Buyer - search by a buyer that have not bought items", async () => {
    var response = await request.get("/item/getbycond/buyer/10000")
    expect(response.status).toBe(200)
    expect(response.body.length).toBe(0)
})

test("Search by Buyer - search by a buyer that do not exist", async () => {
    var response = await request.get("/item/getbycond/buyer/not a user")
    expect(response.status).toBe(200)
    expect(response.body.length).toBe(0)
})

test("Search by Condition - search item that require refund", async () => {
    var response = await request.get("/item/getbycond/admin/1")
    expect(response.status).toBe(200)
})

test("Search by Condition - search item that require admin attention", async () => {
    var response = await request.get("/item/getbycond/refund/1")
    expect(response.status).toBe(200)
})

test("Search by Condition - search item that are bid by user", async () => {
    var response = await request.get("/item/getbycond/bidder/10")
    expect(response.status).toBe(200)
})

afterAll(async () => {
    await im.removeItem(item_id)
})
