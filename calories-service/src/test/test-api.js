var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../index');
var should = chai.should();

chai.use(chaiHttp);

describe('Users', function() {
    it('should list ALL users on /users GET', function(done) {
        chai.request('http://localhost:4003')
            .get('/api/v1/users')
            .end(function(err, res){
                res.should.have.status(200);
                done();
            });
    });
});

describe('User Calories', function() {
    it('should list ALL calories on /user/<id>/calories GET', function(done) {
        chai.request('http://localhost:4003')
            .get('/api/v1/user/1/calories')
            .end(function(err, res){
                res.should.have.status(200);
                done();
            });
    });
    it('should add a SINGLE user on /user/<id>/calories POST', function(done) {
        chai.request('http://localhost:4003')
            .post('/api/v1/user/1/calories')
            .send({
                "id": 0,
                "meal": "test2",
                "note": "test",
                "amount": 100,
                "eatTime": 1462171592000
            })
            .end(function(err, res){
                res.should.have.status(200);
                done();
            });
    });
    it('should update a SINGLE user on /user/<id>/calories PUT');
});


