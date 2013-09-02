// 
// Quick-n-dirty Example API Server for Skedulo intervew questions
//

// Imports
var express = require('express');
var url     = require('url');

// Some sample data
var people = [
	{id: "1", name: "Bill Gates", org: "Microsoft"},
	{id: "2", name: "Steve Jobs", org: "Apple"},
	{id: "3", name: "Barack Obama", org: "Government"},
	{id: "4", name: "Jonathan Doe", org: "ACME"}	
];

var interests = [
	{personId: "1", name: "Skiing"},
	{personId: "1", name: "Philanthropy"},
	{personId: "2", name: "Fonts"},
	{personId: "3", name: "Basketball"}	
];

var skills = [
	{personId: "1", name: "C++"},
	{personId: "1", name: "Basic"},
	{personId: "1", name: "Monopoly"},
	{personId: "2", name: "Turtlenecks"},
	{personId: "2", name: "Instagram"},
	{personId: "3", name: "Basketball"},
	{personId: "3", name: "Cycling"}	
];

var app = express();

// Allow static content
app.configure(function(){
	app.use(express.static(__dirname + '/public'));
});

// API to get a list of people
app.get('/people', function(req, res){
	res.json( people )
});

// API to get the id of the richest person
app.get('/richest', function(req,res){
	res.json({
		richestPerson: 1
	});
});

// Get a list of interest for the given people ids. (/interests?personIds=1,2,3)
app.get('/interests', function(req, res){

	var query = url.parse( req.url, true ).query
	if( ! query.personIds ){
		return res.send(500, "Parameter 'personIds' required")
	}

	var personIds = query.personIds.split(",");

	var results = interests.filter(function(interest){		
		return personIds.indexOf( interest.personId ) >= 0;
	});

	res.json( results );
});

// Get a list of skills for the given people ids. (/interests?personIds=1,2,3)
app.get('/skills', function(req, res){

	var query = url.parse( req.url, true ).query
	if( ! query.personIds ){
		return res.send(500, "Parameter 'personIds' required")
	}

	var personIds = query.personIds.split(",");

	var results = skills.filter(function(skill){		
		return personIds.indexOf( skill.personId ) >= 0;
	});

	res.json( results );
});

// Start the server!
app.listen(3000);