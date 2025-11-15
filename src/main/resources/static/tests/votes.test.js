jest
    .dontMock("fs")
    .dontMock("jquery");

const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(path.basename(__dirname), '../static/tests/perks-page-test.html'), 'utf8');

const $ = require("jquery");
global.$ = global.jQuery = $;

const initVotes = require("../js/votes.js");



describe("Test Suite for successful upvotes and downvotes: ", () => {
    let upvote_button;
    let downvote_button;
    let perk_rating;
    beforeEach(() => {
        document.documentElement.innerHTML = html;

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
        });

        initVotes();

        upvote_button = $("#upvote_button");
        downvote_button = $("#downvote_button");
        perk_rating = $("#rating");
    });

    afterEach(() => {
        jest.resetAllMocks();
    })

    test("Test Click Upvote", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0");

        upvote_button.click(); //simulate picking the upvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("1");

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/upvote`,
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

    test("Test Click Upvote Twice", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0")

        upvote_button.click(); //simulate picking the upvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/upvote`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": "test-token",
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "same-origin"
            }
        );

        upvote_button.click(); //simulate picking the upvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("0")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/upvote`,
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

    test("Test Click Downvote", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0")

        downvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("-1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/downvote`,
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

    test("Test Click Downvote Twice", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0")

        downvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("-1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/downvote`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": "test-token",
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "same-origin"
            }
        );

        downvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("0")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/downvote`,
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

    test("Test Click Upvote and Downvote", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0")

        upvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/upvote`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": "test-token",
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "same-origin"
            }
        );

        downvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("-1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/downvote`,
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

    test("Test Click Upvote and Downvote", () => {
        //Test the perk is what we expect it to be
        expect(perk_rating.text()).toBe("0")

        downvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("-1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/downvote`,
            {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": "test-token",
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                credentials: "same-origin"
            }
        );

        upvote_button.click(); //simulate picking the downvote button

        //Test the UI updates
        expect(perk_rating.text()).toBe("1")

        //Test we made the correct fetch call
        expect(fetch).toHaveBeenCalledWith(
            `/perks/3/upvote`,
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

