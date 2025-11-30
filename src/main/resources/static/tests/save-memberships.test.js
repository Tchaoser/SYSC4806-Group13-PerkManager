jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../static/tests/membership-page-test.html'), 'utf8');

const $ = require("jquery");
global.$ = global.jQuery = $;

const initPerksSave = require("../js/save-memberships.js");



describe("Test Suite for successful membership saving: ", () => {
    let save_button;
    let save_div;
    beforeEach(() => {
        document.documentElement.innerHTML = html;

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
        });

        initPerksSave();

        save_button = $("#save_button");
        save_div = document.getElementById("save_div");
    });

    afterEach(() => {
        jest.resetAllMocks();
    })

    test("Test Click Save", () => {

        save_button.click(); //simulate picking the upvote button

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/memberships/3/save`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": "test-token",
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "same-origin"
            }
        );
    });

});