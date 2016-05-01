'use strict';

var promise = require('bluebird');
var options = {
    promiseLib: promise
};
var pgp = require('pg-promise')(options);
var db = pgp('postgres://test_user:test95@localhost:5432/calories_test');
var _ = require('lodash');
var crypto = require('crypto');
var jwt = require('jsonwebtoken');
var pageSize = 100;
var privateJwtKey = "hsywca52nalq@@b";

exports.createUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * user (User)
   **/

    db.one("insert into users(user_name, email, user_type, daily_calories) values(${name}, ${email}, ${dailyCalories}, ${userType}) returning user_id", args.user)
        .then(function (data) {
            args.user.id = data.user_id;

            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(args.user, null, 2));
            console.log(data.user_id); // print new user id;
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });
/*  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 123,
  "id" : 123456789,
  "userType" : 123,
  "email" : "aeiou"
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.createUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorie (Calorie)
  **/

    var userId = args.id.value;
    var cl = _.assignIn({}, args.calorie.value);

    db.one("insert into calories(user_id, meal, note, calorie_num, eat_time) values($1, $2, $3, $4, $5) returning calorie_id",
           [userId, cl.meal, cl.note, cl.amount, new Date(cl.eatTime)])
        .then(function (data) {

            cl.id = data.calorie_id;

            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(cl, null, 2));
            console.log(data.calorie_id); // print new user id;
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });


 /* var examples = {};
  examples['application/json'] = {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 123,
  "id" : 123456789
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.deleteUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  // no response value expected for this operation

    var userId = args.id;
    db.none("update users set delete=TRUE where user_id=$1", userId)
        .then(function () {
            // success;
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

  res.end();
};

exports.deleteUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorieId (Long)
  **/
  // no response value expected for this operation

    var calorieId = args.calorieId;
    db.none("update calories set delete=TRUE where calorie_id=$1", calorieId)
        .then(function () {
            // success;
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

  res.end();
};

exports.getUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/

    db.one("select user_id, user_name, email, user_type, daily_calories from users where user_id=$1 and not deleted",
           args.id)
        .then(function (data) {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(function(el){
                return {
                    id: el.user_id,
                    name: el.user_name,
                    email: el.email,
                    dailyCalories: el.daily_calories,
                    userType: el.user_type
                };
            }(data)), null, 2);
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

/*  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 123,
  "id" : 123456789,
  "userType" : 123,
  "email" : "aeiou"
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.getUserCaloriesFilter = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * last (Long)
  * fromDate (Long)
  * toDate (Long)
  * fromTime (Long)
  * toTime (Long)
  **/
    db.any("select calorie_id, meal, note, calorie_num, eat_time from calories where user_id=$1 and not deleted" +
           " and eat_time >= $2 and eat_time <=$3" +
           " and date_part('hour', eat_time) >= date_part('hour', $4) and date_part('minute', eat_time) >= date_part('minute', $4)" +
           " and date_part('hour', eat_time) <= date_part('hour', $5) and date_part('minute', eat_time) <= date_part('minute', $5)" +
           " and ($6 = 0 or eat_time < to_timestamp($6)) order by eat_time desc limit ($7)",
           [args.id, new Date(args.fromDate), new Date(args.toDate), new Date(args.fromTime), new Date(args.toTime), args.last, pageSize])
        .then(function (data) {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(_.map(data, function(el){
                return {
                    id: el.calorie_id,
                    meal: el.meal,
                    note: el.note,
                    amount: el.calorie_num,
                    eatTime: el.eat_time.getTime()
                };
            }), null, 2));
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

/*  var examples = {};
  examples['application/json'] = [ {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 123,
  "id" : 123456789
} ];

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.getUserCaloriesList = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * last (Long)
  **/

    db.any("select calorie_id, meal, note, calorie_num, eat_time from calories where user_id=$1 and not deleted" +
           " and ($2 = 0 or eat_time < to_timestamp($2)) order by eat_time desc limit ($3)",
           [args.id, args.last, pageSize])
        .then(function (data) {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(_.map(data, function(el){
                return {
                    id: el.calorie_id,
                    meal: el.meal,
                    note: el.note,
                    amount: el.calorie_num,
                    eatTime: el.eat_time.getTime()
                };
            }), null, 2));
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

/*  var examples = {};
  examples['application/json'] = [ {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 123,
  "id" : 123456789
} ];

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.getUserList = function(args, res, next) {
  /**
   * parameters expected in the args:
  * last (String)
  **/

    db.any("select user_id, user_name, email, user_type, daily_calories from users where not deleted" +
           " and ($1 is null or user_name > $1) order by user_name desc limit ($2)",
           [args.last, pageSize])
        .then(function (data) {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(_.map(data, function(el){
                return {
                    id: el.user_id,
                    name: el.user_name,
                    email: el.email,
                    dailyCalories: el.daily_calories,
                    userType: el.user_type
                };
            }), null, 2));
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

/*
  var examples = {};
  examples['application/json'] = [ {
  "name" : "aeiou",
  "dailyCalories" : 123,
  "id" : 123456789,
  "userType" : 123,
  "email" : "aeiou"
} ];

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

function createHash(str){
    return crypto.createHash('md5').update(str).digest('hex');
}

exports.login = function(args, res, next) {
  /**
   * parameters expected in the args:
  * info (LoginInfo)
  **/

    db.one("select user_id, user_name, email, user_type, daily_calories, psw_hash from users where email=$1 and not deleted",
           args.info.value.email)
        .then(function (data) {
            // success;
            var psw_hash = createHash(args.info.value.password);
            if(data.psw_hash == psw_hash){

                var user = {
                    id: data.user_id,
                    name: data.user_name,
                    email: data.email,
                    dailyCalories: data.daily_calories,
                    userType: data.user_type
                };

                res.setHeader('Content-Type', 'application/json');
                res.end(JSON.stringify({
                    acces_token: jwt.sign({ userId: user.id }, privateJwtKey, {expiresIn: '360d'}),
                    user: user
                }), null, 2);
            }
            else{
                console.log("ERROR: wrong psw."); // print error;
                console.log(data.psw_hash); // print error;
                console.log(psw_hash); // print error;
            }
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

/*  var examples = {};
  examples['application/json'] = {
  "accessToken" : "aeiou",
  "user" : {
    "name" : "aeiou",
    "dailyCalories" : 123,
    "id" : 123456789,
    "userType" : 123,
    "email" : "aeiou"
  }
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }

*/
};

exports.signup = function(args, res, next) {
  /**
   * parameters expected in the args:
  * info (SignUpInfo)
   **/

    var user = {
        name: args.info.value.name,
        email: args.info.value.email,
        dailyCalories: 2500,
        userType: 1 //user
    };

    var psw_hash = createHash(args.info.value.password);

    db.one("insert into users (user_name, email, user_type, daily_calories, psw_hash) values($1, $2, $3, $4, $5) returning user_id",
           [user.name, user.email, user.userType, user.dailyCalories, psw_hash])
        .then(function (data) {
            // success;
            user.id = data.user_id;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify({
                acces_token: jwt.sign({ userId: user.id }, privateJwtKey, {expiresIn: '360d'}),
                user: user
            }), null, 2);
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });

  /*var examples = {};
  examples['application/json'] = {
  "accessToken" : "aeiou",
  "user" : {
    "name" : "aeiou",
    "dailyCalories" : 123,
    "id" : 123456789,
    "userType" : 123,
    "email" : "aeiou"
  }
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }*/


};

exports.updateUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * user (User)
  **/

    /*var user = {
        id: args.body.id,
        name: args.body.name,
        email: args.body.email,
        dailyCalories: args.body.dailyCalories,
        userType: args.body.userType
     };*/
    var user = _.assign({}, args.user);
    db.none("update users set user_name=$2, daily_calories=$3  where user_id=$1", user.id)
        .then(function () {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(user, null, 2));
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });


/*  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 123,
  "id" : 123456789,
  "userType" : 123,
  "email" : "aeiou"
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
*/

};

exports.updateUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorie (Calorie)
  **/

    var userId = args.id;
    var calorie = _.assign({}, args.calorie);
    db.none("update calories set meal=$2, note=$3, calorie_num=$4, eat_time=$5 where calorie_id=$1",
            [calorie.id, calorie.meal, calorie.note, calorie.amount, new Date(calorie.eatTime)])
        .then(function () {
            // success;
            res.setHeader('Content-Type', 'application/json');
            res.end(JSON.stringify(calorie, null, 2));
        })
        .catch(function (error) {
            console.log("ERROR:", error); // print error;
        });


  var examples = {};
  examples['application/json'] = {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 123,
  "id" : 123456789
};

  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }


};
