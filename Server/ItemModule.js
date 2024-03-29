const Item_Database = require('./ItemDB.js')

function Item_module(){
    console.log("initializing Item Module")
    this.item_db = new Item_Database("mongodb://localhost:27017", "F5")
}

Item_module.prototype.postItem = function(item){
    return this.item_db.postItem(item)
        
}

//resolve to true if successful, see ItemDB
Item_module.prototype.updateItem = function(item){
    return this.item_db.updateItem(item)
}

Item_module.prototype.getItemByID = function(itemId){
    return this.item_db.getItemById(itemId).then(item => {
        if(item != null && Object.prototype.hasOwnProperty.call(item, "ItemID") && Object.prototype.hasOwnProperty.call(item, "sellerID")){
            return this.item_db.getUserName(item.sellerID).then(seller_name => {
                item.seller_name = seller_name
                return item
            })
        }
        else{
            return item
        }
    })
}

Item_module.prototype.getItemByID_history = function(itemId, userID){
    return this.item_db.getItemById(itemId).then(item => {
        if(item != null && userID != null&& Object.prototype.hasOwnProperty.call(item, "ItemID") && Object.prototype.hasOwnProperty.call(item, "catagory")){
            this.item_db.saveHistory(userID, item)
            return this.item_db.getUserName(item.sellerID).then(seller_name => {
                item.seller_name = seller_name
                return item
            })
        }
        else{
            return item
        }
    })
}

//Get item by buyer will query items where the user have won the bid(item expired && highest bidder)
Item_module.prototype.getItemByBuyer = function(buyer_id){
    if(buyer_id == null){
        return new Promise((resolve, reject) => {
            resolve([])
        })
    }
    var query = {$and : [
        {currentPriceHolder : buyer_id},
        {expired : "true"}
    ]}
    return this.item_db.getItemByCondition(query)
}

//get item by bidder will query items where the bidder current have a bid on, whether or not the bid is the highest bid
Item_module.prototype.getItemByBidder = function(bidder_id){
    if(bidder_id == null){
        return new Promise((resolve, reject) => {
            resolve([])
        })
    }
    var query = {$and : [
        {$or : [
            {bidders : bidder_id},
            {currentPriceHolder : bidder_id}
        ]},
        {expired : "false"}
    ]}
    return this.item_db.getItemByCondition(query)
}
    

Item_module.prototype.getItemBySeller = function(seller_id){
    if(seller_id == null){
        return new Promise((resolve, reject) => {
            resolve([])
        })
    }
    var query = {sellerID : seller_id}
    return this.item_db.getItemByCondition(query)
}

Item_module.prototype.getItemByCatagory = function(Catagory){
    if(Catagory == null){
        return new Promise((resolve, reject) => {
            resolve([])
        })
    }
    var query = {$and : [
        {catagory : Catagory},
        {expired : "false"}
    ]}
    return this.item_db.getItemByCondition(query)
}

//return any items where needadmin field is true
Item_module.prototype.getItemByAdmin = function(){
    var query = {needAdmin : 'true'}
    return this.item_db.getItemByCondition(query)
}

//return any item where refund field is true
Item_module.prototype.getItemByRefund = function(){
    var query = {refund : 'true'}
    return this.item_db.getItemByCondition(query)
}

Item_module.prototype.searchForItem = function(key_word){
    if(key_word == null){
        return new Promise((resolve, reject) => {
            resolve([])
        })
    }
    return this.item_db.searchItem(key_word)
}

Item_module.prototype.removeItem = function(itemID){
    if(itemID == null){
        return new Promise((resolve, reject) => {
            resolve(false)
        })
    }
    return this.item_db.removeItem(itemID)
}

//routine for checking item expire time and updating process
Item_module.prototype.updateExpireStatus = function(){
    var query = {$and:[
                    {$expr: { $lte: [ { $toDouble: "$timeExpire" }, Date.now()/1000 ] } },
                    {expired : "false"}
                    ]  }

    return this.item_db.getItemByCondition(query).then(expired_items => {
        expired_items.forEach(element => {
            console.log(element)
            if(Object.prototype.hasOwnProperty.call(element, "currentPriceHolder") && element.currentPriceHolder != "no one bid yet"){
                //item has been bought charge the buyer
                this.item_db.chargeUser(element.currentPriceHolder, element.currentPrice).then(success => {
                    if(success){
                        //payment success
                        update = {
                            ItemID : element.ItemID,
                            status : "sold",
                            expired : "true"
                        }
                        this.item_db.updateItem(update)
                    }
                    else{
                        //payment error
                        update = {
                            ItemID : element.ItemID,
                            status : "payment_pending",
                            expired : "true"
                        }
                        this.item_db.updateItem(update)
                    }
                })

            }
            else{
                //item expired with no bidder
                update = {
                    ItemID : element.ItemID,
                    status : "expired",
                    expired : "true"
                }
                this.item_db.updateItem(update)
            }
        });
    })
}

Item_module.prototype.compleateSale = function(itemID){
    return this.item_db.getItemById(itemID).then(item => {
        if(item != null && item.status == "sold"){
            console.log("[ItemModule] compleating sale for item " + itemID)
            return this.item_db.chargeUser(item.sellerID, "-" + item.currentPrice).then(success => {
                update = {
                    ItemID : item.ItemID,
                    status : "complete",
                }
                return this.item_db.updateItem(update)
            })
        }
        else{
            return new Promise((resolve, reject) => {
                console.log("[ItemModule] failed to compleate sale, item is not sold")
                resolve(false)
            })
        }
    })
}

Item_module.prototype.bidItem = function(item_ID, bid_userID, bid_price){
    console.log("[ItemModule] user " + bid_userID + " is bidding " + item_ID + " with price " + bid_price)
    if(item_ID != null && bid_userID != null && bid_price != null){
        update = {
            $push: { bidders : bid_userID},
            $set : {
                currentPrice : bid_price.toString(),
                currentPriceHolder : bid_userID,
            }
        }
        return this.item_db.bidItem(item_ID, update)
    }
    else{
        return new Promise((resolve,reject) => {
            resolve(false)
        })
    }
}

module.exports = Item_module



//Testing codes


// var im = new Item_module()
// im.compleateSale("119")

// im.getItemByBidder("002").then(items => {
//     console.log(items)
// })

// im.bidItem("121", "002", "505")


// count = 1
// setInterval(() => {
//     im.updateExpireStatus()
// }, 5000)
// var Item1 = {
//     "name" : "table and desk",
//     "Buyer" : "222",
//     "Seller" : "333",
//     "Description" : "none"
// }

// var Item2 = {
//     "name" : "this is also a table and a desk but is hav a very very very very long name to decrease score",
//     "Buyer" : "222",
//     "Seller" : "333",
//     "Description" : "none"
// }

// var Item3 = {
//     "name" : "a desk",
//     "Buyer" : "222",
//     "Seller" : "333",
//     "Description" : "none"
// }





//  im.postItem(Item1)
//  im.postItem(Item2)
//  im.postItem(Item3)

// im.getItemBySeller("333").then(items => {
//     console.log(items)
// })

//im.item_db.createIndex()

// var Item1_1 = {
//     "ItemID" : '1657310993397',
//     "name" : "this is a updated item item",
//     "Buyer" : "222",
//     "Seller" : "333",
//     "Description" : "this is an upodated description"
// }

// im.updateItem(Item1_1)

// im.getItemBySeller("333").then(items => {
//     console.log(items)
// })

// im.searchForItem("desk and table").then(items => {
//     console.log(items)
// })
