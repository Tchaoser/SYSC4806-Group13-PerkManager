jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../templates/fragments/navbar.html'), 'utf8');

const $ = require("jquery");
global.$ = global.jQuery = $;

describe("Test Suite for navbar.html: ", () => {
    beforeEach(() => {
        document.documentElement.innerHTML = html;
    });

    test("Verify contents of navbar left side", () => {
        const leftside = document.getElementById("left_side");
        expect(leftside.innerHTML).toContain("<a th:href=\"@{/}\">Home</a>");
        expect(leftside.innerHTML).toContain("<a th:href=\"@{/perks}\">Perks</a>");
        expect(leftside.innerHTML).toContain("<a th:href=\"@{/memberships}\">Memberships</a>");
        expect(leftside.innerHTML).toContain("<a th:href=\"@{/products}\">Products</a>");
    });

    test("Verify Contents of right side when not authorized", () => {
        const not_authorized = document.getElementById("not_authorized");
        expect(not_authorized.getAttribute("sec:authorize")).toBe("!isAuthenticated()")
        expect(not_authorized.innerHTML).toContain("<a th:href=\"@{/login}\">Login</a>");
        expect(not_authorized.innerHTML).toContain("<a th:href=\"@{/signup}\">Sign Up</a>");
    });

    test("Verify right side authorized section checks user is authorized", () =>{
        const authorized = document.getElementById("authorized")
        expect(authorized.getAttribute("sec:authorize")).toBe("isAuthenticated()")
    });

});