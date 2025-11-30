function initMembershipsSave() {
    document.querySelectorAll(".membership-save-forms").forEach(container => {
        const loggedIn = container.dataset.loggedIn === "true";
        const saveState = parseInt(container.dataset.saveState) || 0;

        const saveBtn = container.querySelector('form[action*="/save"] button');

        const membershipId = container.dataset.membershipId;
        const csrfHeader = container.dataset.csrfHeader;
        const csrfToken = container.dataset.csrfToken;

        let currentSaveState = saveState;

        const updateButtons = () => {
            saveBtn.classList.toggle("saved", currentSaveState === 1);
        };

        const handleSave = async () => {
            if (!loggedIn) return alert("You must be logged in to save memberships!");

            const oldSaveState = currentSaveState;

            currentSaveState = currentSaveState === 1 ? 0 : 1;
            
            updateButtons();

            try {
                const response = await fetch(`/memberships/${membershipId}/save`, {
                    method: "POST",
                    headers: {
                        [csrfHeader]: csrfToken,
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    credentials: "same-origin"
                });

                if (!response.ok) throw new Error("Membership failed to save!");
            } catch (err) {
                console.error(err);
                alert("Membership failed to save. Reverting...");
                currentSaveState = oldSaveState;
                updateButtons();
            }
        };

        saveBtn.addEventListener("click", e => {
            e.preventDefault();
            handleSave();
        });

        updateButtons();
    });
}

document.addEventListener("DOMContentLoaded", initMembershipsSave);

if (typeof module === "object") {
    module.exports = initMembershipsSave;
}
