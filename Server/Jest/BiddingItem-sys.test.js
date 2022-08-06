const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
const ItemModule = require("../ItemModule")

var item_id
beforeAll(async () => {
    var response = await request.post("/item/postitem/").send({name: 'Desk' , currentPriceHolder : "1", Price : "10", expired : "false"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Updating Item - update exsting item", async () => {
    var response = await request.put("/item/bidItem/").send({ItemID: item_id , currentPriceHolder : "2" , currentPrice : "500"}).set('Accept', 'application/json')
    expect(response.status).toBe(200)
    var response = await request.get("/item/getbyid/" + item_id)
    expect(response.body.currentPrice).toBe("500")
})

test("Updating Item - update non existing item", async () => {
    //user now exist as the first test would have created the use
    var response = await request.put("/item/updateitem/").send({ItemID: "not a id" , currentPriceHolder : "2" , currentPrice : "500"}).set('Accept', 'application/json')
    expect(response.status).toBe(404)
})

test("Updating Item - update no item id", async () => {
    //user now exist as the first test would have created the use
    var response = await request.put("/item/updateitem/").send({currentPrice : "500"}).set('Accept', 'application/json')
    expect(response.status).toBe(400)
})


afterAll(async () => {
    const im = new ItemModule()
    await im.removeItem(item_id)
})