jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../templates/profile.html'), 'utf8');

const $ = require("jquery");
global.$ = global.jQuery = $;


describe("Test Suite for profile.html: ", () => {
    beforeEach(() => {
        document.documentElement.innerHTML = html;
    });

    test("Test contents of Guest View", () => {
        const guestView = document.getElementById("guest_view");
        expect(guestView).toBeTruthy()
        expect(guestView.innerHTML).toContain("You are viewing as guest.");
        expect(guestView.innerHTML).toContain("<a th:href=\"@{/login}\">Login</a> or <a th:href=\"@{/signup}\">Sign up</a>.");
    });

    test("Test contents of User Info", () => {
        //Check User Info contents are correct
        const userInfo = document.getElementById("user_info");
        expect(userInfo).toBeTruthy()
        expect(userInfo.innerHTML).toContain("<strong>Username:</strong> <span th:text=\"${account.username}\">username</span>");

        //Check User Info Contents exist in authenticated div
        const authenticated = document.getElementById("authenticated");
        expect(authenticated.innerHTML).toContain(userInfo.outerHTML)
    });

    test("Test header exists", () => {
        expect(document.documentElement.innerHTML).toContain("<head th:replace=\"fragments/head :: head('User Profile')\"></head>");
    });

    test("Test navbar exists", () => {
        expect(document.documentElement.innerHTML).toContain("<div th:replace=\"fragments/navbar :: navbar\"></div>");
    });

    test("Test footer exists", () => {
        expect(document.documentElement.innerHTML).toContain("<div th:replace=\"fragments/footer :: footer\"></div>");
    });

});
