/**
 * Main application routes
 */

'use strict';

var errors = require('./components/errors');
var path = require('path');

module.exports = function(app) {

  // Insert routes below
  // app.use('/api/things', require('./api/thing'));

  app.get('/api/redirector',function(req,res){
    res.redirect(301, '/api/redirectHandler');
  })

  app.get('/api/redirectHandler', function (req, res) {
    res.send({"result": "Life is good from GET RedirectHandler"})
  })


  // redirect post calls

  app.post('/api/redirector',function(req,res){
    console.log(req.body);
    res.redirect(307, '/api/redirectHandler');
  })

  app.post('/api/redirectHandler', function (req, res) {
    console.log('in post redirector');
    res.json({"result": "Life is good from POST RedirectHandler"})
  })

    // All undefined asset or api routes should return a 404
  app.route('/:url(api|auth|components|app|bower_components|assets)/*')
   .get(errors[404]);

  // All other routes should redirect to the index.html
  app.route('/*')
    .get(function(req, res) {
      res.sendFile(path.resolve(app.get('appPath') + '/index.html'));
    });
};
