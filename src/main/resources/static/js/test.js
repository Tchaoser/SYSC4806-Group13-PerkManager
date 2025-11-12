const fs = require("fs");
const { JSDOM } = require("jsdom");



describe("Test Suite", function() {
    it("test case 1", function() {
        const htmlFile = fs.readFileSync("static/js/test.html", 'utf8')
        const dom = new JSDOM(htmlFile)
        const document = dom.window.document
        const button = document.getElementById('my-component')
        expect(button).toBeTruthy()
        expect(button.textContent).toBe("Click Me")

    })
})





