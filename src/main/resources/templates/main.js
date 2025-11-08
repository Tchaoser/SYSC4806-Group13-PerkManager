function calculatesum(a, b){
    return a + b;
}

//Test 2

describe("Test Suite 1", function() {
    it("test suite 1", function() {
        const result = calculatesum(5, 10)
        expect(result).toBe(15)
    })
});