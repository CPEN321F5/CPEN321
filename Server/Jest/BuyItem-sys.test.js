const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
const ItemModule = require("../ItemModule")

var item_id
beforeAll(async () => {
    var response = await request.post("/item/postitem/").send({name: 'Jet' , sellerID : "1", Price : "10"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    im = new ItemModule()
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Buy Item - buying a exisitng item", async () => {
    var response = await request.put("/item/updateitem/").send({ItemID: item_id , buyerID : "1", status : "sold"}).set('Accept', 'application/json')
    expect(response.status).toBe(200)
    response = await request.get("/item/getbyid/" + item_id)
    expect(response.body.status).toBe("sold")
})


test("Buy Item - buying a non-exisitng item", async () => {
    //user now exist as the first test would have created the use
    var response = await request.put("/item/updateitem/").send({ItemID: "not a id" , buyerID : "1", status : "sold"}).set('Accept', 'application/json')
    expect(response.status).toBe(404)
})

afterAll(async () => {
    await im.removeItem(item_id)
})