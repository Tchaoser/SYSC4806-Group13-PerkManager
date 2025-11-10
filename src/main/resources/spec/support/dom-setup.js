const { JSDOM } = require('jsdom');
const { window } = new JSDOM('<!doctype html><html><body></body></html>');

global.window = window;
global.document = window.document;
global.navigator = { userAgent: 'node.js' }; // Optional: for some libraries that check navigator

global.jQuery = global.$ = require('jquery')(window);
require('jasmine-jquery'); // Load jasmine-jquery after jQuery is available