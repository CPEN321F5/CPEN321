const ItemModule = require("../../ItemModule")

//setting up module under test
const im = new ItemModule()


var inti_item = {
    startPrice          : "1",
    needAdmin           : "true",
    description         : "A test product",
    timeLast            : "2",
    currentPrice        : "4",
    location_lon        : "38.49550333333333",
    stepPrice           : "1",
    currentPriceHolder  : "2",
    timeExpire          : "1657667094",
    postTime            : "12/07/2022 14:04:54",
    adminResponse       : "Waiting For Admin To Resolve Dispute!",
    sellerID            : "1", 
    name                : "Test Item",
    deposit             : "1",
    refundDescrition    : "",
    location_lat        : "7.050373333333332",
}
var init_item_id
var item_id_1
var item_id_2

beforeAll(async () => {
    im.postItem(inti_item).then(item => {
        init_item_id = item.ItemID
    })
    //init intm_db index for search
    im.item_db.createIndex()
    //mongodb seem to have some delay before document being created in new collection and being able to updated, adding a slight delay to resolve that
    await new Promise((resolve, reject) => setTimeout(resolve, 2000));
})

//adding an item that later tests can use






//testing the PostItem interface of Item module

test('Testing Item module - PostItem interface - Posting new Item', () => {
    var new_item = {
        startPrice          : "1",
        needAdmin           : "false",
        description         : "A test product",
        timeLast            : "2",
        currentPrice        : "4",
        location_lon        : "38.49550333333333",
        stepPrice           : "1",
        currentPriceHolder  : "0000",
        timeExpire          : "1657667094",
        postTime            : "12/07/2022 14:04:54",
        adminResponse       : "Waiting For Admin To Resolve Dispute!",
        sellerID            : "0001", 
        name                : "new Test Item",
        deposit             : "1",
        refundDescrition    : "",
        refund              : "true",
        location_lat        : "7.050373333333332",
        catagory   : "Furniture",
        expired    : "false"
    }

    return im.postItem(new_item).then(result => {
        expect(result).toEqual(expect.objectContaining(new_item))
        expect(result.ItemID).toBeDefined()
        item_id_1 = result.ItemID
    })
})

test('Testing Item module - PostItem interface - Posting empty Item ', () => {
    //the only field in the object should be the itemID
    return im.postItem({}).then(result => {
        expect(result.ItemID).toBeDefined()
        item_id_2 = result.ItemID
    })
})






//testing the UpdateItem interface of Item module
test('Testing Item module - UpdateItem - Successful profile update', () => {
    console.log(init_item_id)
    var update = {
        ItemID : init_item_id,
        startPrice  : "999999999",
        currentPriceHolder    : "2",expired    : "true",
        catagory   : "Furniture"
    }
    return im.updateItem(update).then(result => expect(result).toBeTruthy())
})

test('Testing Item module - UpdateItem - userID only, no real update', () => {
    var update = {
        ItemID : init_item_id
    }
    return im.updateItem(update).then(result => expect(result).toBeTruthy())
})

test('Testing Item module - UpdateItem - no itemID in update request', () => {
    var update = {
        startPrice  : "999999999"
    }
    return im.updateItem(update).then(result => expect(result).toBeFalsy())
})

test('Testing Item module - UpdateItem - adding new field in profile', () => {
    var update = {
        ItemID : init_item_id,
        newField  : "this is a new field"
    }
    return im.updateItem(update).then(result => expect(result).toBeTruthy())
})

test('Testing Item module - UpdateItem - null input ', () => {
    return im.updateItem().then(result => expect(result).toBeFalsy())
})






//testing the getItemByBuyer interface of Item module


test('Testing Item module - getItemByBuyer interface - Getting bought item list for user that have bought items', () => {
    //2 was added as the buyer of an item
    return im.getItemByBuyer("2").then(result => expect(result.length).toBeGreaterThan(0))
})

test('Testing Item module - getItemByBuyer interface - Getting bought item list for user that have never bought items', () => {
    return im.getItemByBuyer("3").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemByBuyer interface - non-existent userID ', () => {
    return im.getItemByBuyer("2131231231").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemByBuyer interface - invalid user ID ', () => {
    return im.getItemByBuyer().then(result => expect(result.length).toBe(0))
})






//testing the getItemBySeller interface of Item module
test('Testing Item module - getItemBySeller interface - Getting sold item list for user that have sold items', () => {
    //1 was added as the seller of an item
    return im.getItemBySeller("1").then(result => expect(result.length).toBeGreaterThan(0))
})

test('Testing Item module - getItemBySeller interface - Getting sold item list for user that have never sold items', () => {
    return im.getItemBySeller("3").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemBySeller interface - non-existent userID ', () => {
    return im.getItemBySeller("2131231231").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemBySeller interface - invalid user ID ', () => {
    return im.getItemBySeller().then(result => expect(result.length).toBe(0))
})



//testing the getItemByCategory interface of Item module

test('Testing Item module - getItemByCategory interface - Getting a list of item under a category', () => {
    return im.getItemByCatagory("Furniture").then(result => expect(result.length).toBeGreaterThan(0))
})

test('Testing Item module - getItemByCategory interface - Getting a list of item under a invalid category', () => {
    return im.getItemByCatagory("Nuclear Missile").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemByCategory interface - empty input ', () => {
    return im.getItemByCatagory().then(result => expect(result.length).toBe(0))
})


//testing get item by admin
test('Testing Item module - getItemByCategory interface - Getting a list of item under a category', () => {
    return im.getItemByAdmin().then(result => expect(result.length).toBeGreaterThan(0))
})


//testing get item by admin
test('Testing Item module - getItemByCategory interface - Getting a list of item under a category', () => {
    return im.getItemByRefund().then(result => expect(result.length).toBeGreaterThan(0))
})


test('Testing Item module - UpdateItem - prepare item for search', () => {
    var update = {ItemID : init_item_id, expired : "false"}
    return im.updateItem(update).then(result => expect(result).toBeTruthy())})



//testing the bidItem interface of Item module
test('Testing Item module - bidItem interface - bidding an existing item', () => {
    return im.bidItem(init_item_id, "0001", "500").then(result => expect(result).toBeTruthy)
})

test('Testing Item module - bidItem interface - bidding an non-existing item', () => {
    return im.bidItem("00000000", "0001", "500").then(result => expect(result).toBeFalsy)
})

test('Testing Item module - bidItem interface - null input', () => {
    return im.bidItem(null, "0001", "500").then(result => expect(result).toBeFalsy)
})


//testing the getItemByBidder interface of Item module

test('Testing Item module - getItemByBidder interface - Getting a list of item bid by a user', () => {
    return im.getItemByBidder("0001").then(result => expect(result.length).toBeGreaterThan(0))
})

test('Testing Item module - getItemByBidder interface - Getting a list of item bid by new user', () => {
    return im.getItemByBidder("0002").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemBySeller interface - non-existent userID ', () => {
    return im.getItemByBidder("2131231231").then(result => expect(result.length).toBe(0))
})

test('Testing Item module - getItemByBidder interface - empty input ', () => {
    return im.getItemByBidder().then(result => expect(result.length).toBe(0))
})





//testing the SearchForItem interface of Item module

test('Testing Item module - SearchForItem interface - Search for item that exist', () => {
    return im.searchForItem("Test").then(result => expect(result.length).toBeGreaterThan(0))
})

test('Testing Item module - SearchForItem interface - Searching for item that does not exist', () => {
    return im.searchForItem('LGM-25C Titan Intercontinental Ballistic Missile').then(result => expect(result.length).toBe(0))
})

test('Testing Item module - SearchForItem interface - invalid search string ', () => {
    return im.searchForItem().then(result => expect(result.length).toBe(0))
})

test('Testing Item module - UpdateItem - prepare item for complete', () => {
    var update = {ItemID : init_item_id, status : "sold"}
    return im.updateItem(update).then(result => expect(result).toBeTruthy())
})

//testing the complete order interface of Item module
test('Testing Item module - complete order interface - existing item', () => {
    return im.compleateSale(init_item_id).then(result => expect(result).toBeTruthy)
})

test('Testing Item module - complete order interface - non-existing item', () => {
    return im.compleateSale("not an item id").then(result => expect(result).toBeFalsy)
})

test('Testing Item module - complete order interface - null input', () => {
    return im.compleateSale(null).then(result => expect(result).toBeFalsy)
})

//testing updating existing item
test('Testing Item module - update expireditem routine - running routine ', () => {
    im.item_db.matchItems(1, "Books")
    return im.updateExpireStatus().then(result => expect(1).toBe(1))
})


//testing the getItemByID interface of Item module
var inti_item_updated = {
    startPrice          : "999999999",
    needAdmin           : "true",
    description         : "A test product",
    timeLast            : "2",
    currentPrice        : "500",
    location_lon        : "38.49550333333333",
    stepPrice           : "1",
    currentPriceHolder  : "2",
    timeExpire          : "1657667094",
    postTime            : "12/07/2022 14:04:54",
    adminResponse       : "Waiting For Admin To Resolve Dispute!",
    sellerID            : "1", 
    name                : "Test Item",
    deposit             : "1",
    refundDescrition    : "",
    location_lat        : "7.050373333333332",
    currentPriceHolder  : '0001',
    newField            : 'this is a new field'
}

test('Testing Item module -  getItemByID interface - Getting an item that exist ', () => {
    return im.getItemByID(init_item_id).then(result => expect(result).toEqual(expect.objectContaining(inti_item_updated)))
})

test('Testing Item module -  getItemByID interface - Getting an item that does not exist ', () => {
    return im.getItemByID("this is definetly not a item id").then(result => expect(result).toBeNull())
})

test('Testing Item module -  getItemByID interface - Getting an item using null itemID ', () => {
    return im.getItemByID().then(result => expect(result).toBeNull())
})

test('Testing Item module -  getItemByID with History interface - Getting an item that exist ', () => {
    return im.getItemByID_history(init_item_id, "001").then(result => expect(result).toEqual(expect.objectContaining(inti_item_updated)))
})

test('Testing Item module -  getItemByID with History interface - Getting an item that does not exist ', () => {
    return im.getItemByID_history("this is definetly not a item id", "001").then(result => expect(result).toBeNull())
})

test('Testing Item module -  getItemByID with History interface - Getting an item using null input ', () => {
    return im.getItemByID_history().then(result => expect(result).toBeNull())
})



//testing the RemoveItemByID interface of Item module

test('Testing Item module - RemoveItemByID interface - removing a existing Item', () => {
    return im.removeItem(init_item_id).then(result => expect(result).toBeTruthy())
})

test('Testing Item module - RemoveItemByID interface - removing a non-existing Item', () => {
    return im.removeItem("not a item id").then(result => expect(result).toBeFalsy())
})

test('Testing Item module - RemoveItemByID interface - removing a null itemID', () => {
    return im.removeItem().then(result => expect(result).toBeFalsy())
})

test('Testing Item module - Cleanning up db - 1', () => {
    return im.removeItem(item_id_1).then(result => expect(result).toBeTruthy())
})

test('Testing Item module - Cleanning up db - 2', () => {
    return im.removeItem(item_id_2).then(result => expect(result).toBeTruthy())
})