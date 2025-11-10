describe("jQuery DOM manipulation", () => {
    beforeEach(() => {
        // Load a fixture (e.g., spec/fixtures/my-fixture.html)
        loadFixtures('js/test.html');
    });

    it("should have a button with the correct text", function() {
        expect($('#my-component')).toExist();
    });

    it("should add a class on click", function() {
        $('#my-component.action-button').click();
        expect($('#my-component.action-button')).toHaveClass('clicked');
    });
});


