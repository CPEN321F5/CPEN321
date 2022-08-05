const User_Database = require('./UserDB.js')
//user database for getting browsing history
const Item_Database = require('./ItemDB.js')
//item database for getting items

const HISTORY_LENGTH = 50
const RECOMMEND_ITEM = 10

function recommendationModule(){
    console.log("initializing Recommendation Module")
    this.item_db = new Item_Database("mongodb://localhost:27017", "F5")
    this.user_db = new User_Database("mongodb://localhost:27017", "F5")
}

recommendationModule.prototype.getRecommendItems = async function(userID){

    //got history array length
    var history_arr = await this.getHistoryArray(userID)
    var history_length = history_arr.length

    //no history == brand spanking new user, just show him furniture
    if(history_arr.length == 0){
        console.log("[Recommendation] no browsing history avaliable you must like furniture")
        var query = {$and : [
            {catagory : "Furniture"},
            {expired : "false"}
        ]}
        return this.item_db.getItemByCondition(query)
    }
    
    //calculate weight of each category, out of the number of item to recommend
    var catagory_weight = {}
    for (const category of history_arr) {
        catagory_weight[category] = catagory_weight[category] ? catagory_weight[category] + RECOMMEND_ITEM/history_length : RECOMMEND_ITEM/history_length;
    }
    console.log(catagory_weight)

    //round each category's weight and sample that many item of the specific category from the db
    var recommend_list = []
    for(var catagory in catagory_weight){
        catagory_weight[catagory] = Math.round(catagory_weight[catagory])
        var items = await this.item_db.matchItems(catagory_weight[catagory], catagory)
        recommend_list = recommend_list.concat(items)
    }
    //randomize the array such that the list appears normal on the home screen
    recommend_list = recommend_list.sort((a, b) => 0.5 - Math.random());
    return new Promise((resolve, reject) => {
        resolve(recommend_list)
    })
}

//helper function for getting the browsing history
recommendationModule.prototype.getHistoryArray = async function(userID){
        //get the browsing history
        profile = await this.user_db.getProfile(userID)
        
    
        var history_arr
        if(profile == null){
            console.log("[Recommendation] not a real user ")
            history_arr = []
        }
        else if(Object.prototype.hasOwnProperty.call(profile, "category_history") && profile.category_history.length >= HISTORY_LENGTH){
            //get the last section of the array
            history_arr = profile.category_history.slice(profile.category_history.length - HISTORY_LENGTH)
        }
        else if(Object.prototype.hasOwnProperty.call(profile, "category_history")){
            history_arr = profile.category_history
        }
        else{
            history_arr = []
        }
        return history_arr
}

module.exports = recommendationModule

// rm = new recommendationModule()

// rm.getRecommendItems("001")