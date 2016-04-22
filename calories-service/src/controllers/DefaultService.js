'use strict';

exports.createUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * user (User)
  **/
  
  
  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 1.3579000000000001069366817318950779736042022705078125,
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
  
  
}

exports.createUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorie (Calorie)
  **/
  
  
  var examples = {};
  examples['application/json'] = {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 1.3579000000000001069366817318950779736042022705078125,
  "id" : 123456789
};
  
  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
  
  
}

exports.deleteUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  // no response value expected for this operation
  
  
  res.end();
}

exports.deleteUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorieId (Long)
  **/
  // no response value expected for this operation
  
  
  res.end();
}

exports.getUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  
  
  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 1.3579000000000001069366817318950779736042022705078125,
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
  
  
}

exports.getUserCaloriesList = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * last (Long)
  **/
  
  
  var examples = {};
  examples['application/json'] = [ {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 1.3579000000000001069366817318950779736042022705078125,
  "id" : 123456789
} ];
  
  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
  
  
}

exports.getUserList = function(args, res, next) {
  /**
   * parameters expected in the args:
  * last (String)
  **/
  
  
  var examples = {};
  examples['application/json'] = [ {
  "name" : "aeiou",
  "dailyCalories" : 1.3579000000000001069366817318950779736042022705078125,
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
  
  
}

exports.login = function(args, res, next) {
  /**
   * parameters expected in the args:
  * info (LoginInfo)
  **/
  
  
  var examples = {};
  examples['application/json'] = "aeiou";
  
  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
  
  
}

exports.signup = function(args, res, next) {
  /**
   * parameters expected in the args:
  * info (SignUpInfo)
  **/
  
  
  var examples = {};
  examples['application/json'] = "aeiou";
  
  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
  
  
}

exports.updateUser = function(args, res, next) {
  /**
   * parameters expected in the args:
  * user (User)
  **/
  
  
  var examples = {};
  examples['application/json'] = {
  "name" : "aeiou",
  "dailyCalories" : 1.3579000000000001069366817318950779736042022705078125,
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
  
  
}

exports.updateUserCalorie = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  * calorie (Calorie)
  **/
  
  
  var examples = {};
  examples['application/json'] = {
  "meal" : "aeiou",
  "eatTime" : 123456789,
  "note" : "aeiou",
  "amount" : 1.3579000000000001069366817318950779736042022705078125,
  "id" : 123456789
};
  
  if(Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  }
  else {
    res.end();
  }
  
  
}

