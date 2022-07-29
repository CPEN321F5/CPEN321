const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
var item_id
const ItemModule = require("../ItemModule")
const im = new ItemModule()

beforeAll(async () => {
    im.item_db.createIndex()
    var response = await request.post("/item/postitem/").send({name: 'BattleShip' , sellerID : "9", Price : "999999"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Search by Seller - search by a a seller that have sold items", async () => {
    var response = await request.get("/item/getbycond/seller/9")
    expect(response.status).toBe(200)
    expect(response.body[0].name).toBe("BattleShip")
})

test("Search by Seller - search by a a seller that have not sold items", async () => {
    var response = await request.get("/item/getbycond/seller/10000")
    expect(response.status).toBe(200)
    expect(response.body.length).toBe(0)
})

test("Search by Seller - search by a a seller that do not exist", async () => {
    var response = await request.get("/item/getbycond/seller/not a user")
    expect(response.status).toBe(200)
    expect(response.body.length).toBe(0)
})

afterAll(async () => {
    await im.removeItem(item_id)
})
