const app = require('../server')
const supertest = require('supertest')
const request = supertest(app)
const ItemModule = require("../ItemModule")

var item_id
beforeAll(async () => {
    var response = await request.post("/item/postitem/").send({name: 'Desk' , sellerID : "2", currentPriceHolder : "1", currentPrice : "10", expired : "true", status : "sold"}).set('Accept', 'application/json')
    item_id = response.body.ItemID
    await request.post("/user/signin/2")
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 500));
})

test("Updating Item - update exsting item", async () => {
    var response = await request.put("/item/completesale/" + item_id)
    expect(response.status).toBe(200)
    var response = await request.get("/item/getbyid/" + item_id)
    expect(response.body.status).toBe("complete")
})

test("Updating Item - update non existing item", async () => {
    //user now exist as the first test would have created the use
    var response = await request.put("/item/completesale/notaid/")
    expect(response.status).toBe(400)
})

test("Updating Item - update no item id", async () => {
    //user now exist as the first test would have created the use
    var response = await request.put("/item/updateitem/")
    expect(response.status).toBe(400)
})


afterAll(async () => {
    const im = new ItemModule()
    await im.removeItem(item_id)
})