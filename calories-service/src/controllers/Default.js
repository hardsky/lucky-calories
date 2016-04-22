'use strict';

var url = require('url');


var Default = require('./DefaultService');


module.exports.createUser = function createUser (req, res, next) {
  Default.createUser(req.swagger.params, res, next);
};

module.exports.createUserCalorie = function createUserCalorie (req, res, next) {
  Default.createUserCalorie(req.swagger.params, res, next);
};

module.exports.deleteUser = function deleteUser (req, res, next) {
  Default.deleteUser(req.swagger.params, res, next);
};

module.exports.deleteUserCalorie = function deleteUserCalorie (req, res, next) {
  Default.deleteUserCalorie(req.swagger.params, res, next);
};

module.exports.getUser = function getUser (req, res, next) {
  Default.getUser(req.swagger.params, res, next);
};

module.exports.getUserCaloriesList = function getUserCaloriesList (req, res, next) {
  Default.getUserCaloriesList(req.swagger.params, res, next);
};

module.exports.getUserList = function getUserList (req, res, next) {
  Default.getUserList(req.swagger.params, res, next);
};

module.exports.login = function login (req, res, next) {
  Default.login(req.swagger.params, res, next);
};

module.exports.signup = function signup (req, res, next) {
  Default.signup(req.swagger.params, res, next);
};

module.exports.updateUser = function updateUser (req, res, next) {
  Default.updateUser(req.swagger.params, res, next);
};

module.exports.updateUserCalorie = function updateUserCalorie (req, res, next) {
  Default.updateUserCalorie(req.swagger.params, res, next);
};
