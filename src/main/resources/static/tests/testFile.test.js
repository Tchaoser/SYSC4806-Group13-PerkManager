const sum = require("../js/testFile");


describe("Test Suite for Test File", () => {
    test("test add 2 nums", () => {
        expect(sum(1,2)).toBe(3);
    })
});
