jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../templates/profile.html'), 'utf8');
const $ = require("jquery");
global.$ = global.jQuery = $;
require("jest-fetch-mock").enableMocks()

const profile = require("../js/profile");
const csrfHeaders = profile.csrfHeaders;
const addMembership = profile.addMembership;
const updateMembershipList = profile.updateMembershipList;


describe("Test Suite for profile.js", () => {
    beforeEach(() => {
        document.documentElement.innerHTML = html;
    });

    test("Test ajax call for addMembership", () => {
        const select = document.getElementById('membership-select')
        select.value = 1;
        const ajaxSpy = jest.spyOn($, 'ajax');
        addMembership();
        expect(ajaxSpy).toBeCalledWith({
            url: "test",
            type: "POST",
            headers: csrfHeaders(),
            body: new URLSearchParams(1)
        })
    });
});