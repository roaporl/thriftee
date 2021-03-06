/*
 * Copyright (C) 2013-2016 Benjamin Gould, and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//var _ = require('underscore');
var util = require( 'util' );

var Module = {};

Object.defineProperties(Module, {
  'serialize' : { 'value' : function (obj) {
    var serialized = JSON.stringify(obj);
    return serialized;
  }},
  'deserialize' : { 'value' : function (str) {
    try {
      var obj = JSON.parse(str);
      return obj;
    } catch (e) {
      console.warn('error deserializing JSON', e);
      return null;
    }
  }},
  'to_title_case' : { 'value' : function (str) {
    return str.replace(/([A-Z])/g, ' $1').replace(/^./, function (str) {
      return str.toUpperCase();
    });
  }},
  'extend' : { 'value' : function (){
    if (arguments.length == 0) {
      return null;
    }
    var args = Array.prototype.slice.call(arguments, 0);
    var target = args.shift();
    for (var i in args) {
      if (typeof(args[i]) == 'object') {
        for (var prop in args[i]) {
          if (args[i].hasOwnProperty(prop) && !(target.hasOwnProperty(prop))) {
            target[prop] = args[i][prop];
          }
        }
      }
    }
    return target;
  }},
  'define' : {
    'value' : function (obj, name, value) {
      if (typeof(name) === 'object') {
        var fns = name;
        for (var name in fns) {
          if (fns.hasOwnProperty(name)) {
            Module.define(obj, name, fns[name]);
          }
        }
        return obj;
      }
      if (typeof(name) !== 'string') {
        throw 'illegal argument; second argument must be an object or a string';
      }
      if (typeof(value === 'function')) {
        Object.defineProperty(obj, name, { 'value' : value });
        return obj;
      }
      if (typeof(value) === 'object') {
        Object.defineProperty(obj, name, value);
        return obj;
      }
      var is_public   = (name[0] !== '_');
      var is_readonly = true;
      var is_configurable = false;
      Object.defineProperty(obj, name, { 
        'value' : value, 
        'enumerable' : is_public, 
        'writable' : is_readonly, 
        'configurable' : is_configurable
      });
      return obj;
    }
  },
});

module.exports = Module.extend(Module, util);
