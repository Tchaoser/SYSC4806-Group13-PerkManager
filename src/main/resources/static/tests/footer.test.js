jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../templates/fragments/footer.html'), 'utf8');

const $ = require("jquery");
global.$ = global.jQuery = $;

describe("Test Suite for footer.html: ", () => {
    beforeEach(() => {
        document.documentElement.innerHTML = html;
    });

    test("Verify contents of footer", () => {
        expect(document.body.innerHTML).toContain("" +
            "<hr>\n" +
            "<footer>\n" +
            "    <p>PerkManager © 2025</p>\n" +
            "</footer>")
    });

});
