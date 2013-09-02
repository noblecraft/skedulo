
$(document).ready(function() {

  var db = new PeopleDB();
  db.list(function(people) {
    var table = $('#people');
    for (var personId in people) {
      table.append(_row(people[personId]));
    }

  });

  function _row(person) {
    var row = $('<tr></tr>');
    row.append($('<td></td>').html(person.name));
    row.append($('<td></td>').html(person.org));
    row.append($('<td></td>').html(_commaSeparated(person.skills)));
    row.append($('<td></td>').html(_commaSeparated(person.interests)));
    if (person.richest) {
      row.addClass('richest');
    }
    return row;
  }

  function _commaSeparated(items) {
    var out = '';
    for (var i = 0; i < items.length; i++) {
      out += items[i] + ', ';
    }
    return out.substring(0, out.length - 2);
  }

});


/**
 * Database that returns list of people (with skills & interests)
 * @constructor
 */
function PeopleDB() {
  this._cache = [];
}

PeopleDB.prototype.list = function(callback) {

  var thisObj = this;

  if (this._cache.length == 0) { // a very simplified update policy
    first(_populatePeople).then(_populateInterests, _populateSkills).then(_determineRichest).finally(function() {
      callback(thisObj._cache);
    });

  }

  function _populatePeople(next) {
    $.ajax('/people').done(function (people) {
      for (var i = 0; i < people.length; i++) {
        var person = people[i];
        thisObj._cache[person.id] = {
          name: person.name,
          org: person.org,
          interests: [],
          skills: []
        };
      }
      next();
    });
  }

  function _ids() {
    var out = "";
    for (var id in thisObj._cache) {
      out += id + ",";
    }
    return out.substring(0, out.length - 1);
  }

  function _populateInterests(next) {

    $.ajax('/interests?personIds=' + _ids()).done(function(interests) {

      for (var i = 0; i < interests.length; i++) {
        var interest = interests[i];
        thisObj._cache[interest.personId].interests.push(interest.name);
      }

      next();

    });

  }

  function _populateSkills(next) {

    $.ajax('/skills?personIds=' + _ids()).done(function(skills) {

      for (var i = 0; i < skills.length; i++) {
        var skill = skills[i];
        thisObj._cache[skill.personId].skills.push(skill.name);
      }

      next();

    });

  }

  function _determineRichest(next) {

    $.ajax('/richest').done(function(result) {

      thisObj._cache[result.richestPerson].richest = true;

      next();

    });

  }

}

function first(f) {
  return new Chain(f);
}

/**
 * Used to chain ajax calls to be performed step by step
 * @param f  - the first 'step'
 * @constructor
 */
function Chain(f) {
  this.steps = [f];
}

/**
 * Declares the next function in the chain to be executed.
 * Sychronizes callbacks - the next step in the chain isn't invoked until all calls completed.
 *
 * arguments: a list of functions that will be called at the 'same' time.
 *
 * @returns {*}
 */
Chain.prototype.then = function() {
  var fs = [];
  for (var i = 0; i < arguments.length; i++) {
    fs.push(arguments[i]);
  }
  this.steps.push(function(callback) {
    var countDown = fs.length;
    for (var i = 0; i < fs.length; i++) {
      fs[i](function() {
        if(--countDown == 0) {
          callback();
        }
      });
    }
  });
  return this;
}

/**
 * Declares the final step. also invokes the chain
 * @param f
 */
Chain.prototype.finally = function(f) {
  var thisObj = this;
  this.steps.push(f);
  _proceed(0);
  function _proceed(i) {
    if (i > thisObj.steps.length) {
      return;
    }
    thisObj.steps[i](function() {
      _proceed(i + 1);
    });
  }

}

