const sum = require("../js/testFile");


describe("Test Suite for Test File", () => {

    test("test add 2 nums", () => {
        expect(sum(1,2)).toBe(3);
    })

    test("test add 1 negative and 1 positive", () => {
        expect(sum(-1,2)).toBe(1);
    })

    test("test add 2 negative", () => {
        expect(sum(-1,-2)).toBe(-3);
    })
});
