function initPerksSave() {
    document.querySelectorAll(".perk-save-forms").forEach(container => {
        const loggedIn = container.dataset.loggedIn === "true";
        const saveState = parseInt(container.dataset.saveState) || 0;

        const saveBtn = container.querySelector('form[action*="/save"] button');

        const perkId = container.dataset.perkId;
        const csrfHeader = container.dataset.csrfHeader;
        const csrfToken = container.dataset.csrfToken;

        let currentSaveState = saveState;

        const updateButtons = () => {
            saveBtn.classList.toggle("saved", currentSaveState === 1);
        };

        const handleSave = async () => {
            if (!loggedIn) return alert("You must be logged in to save perks!");

            const oldSaveState = currentSaveState;

            currentSaveState = currentSaveState === 1 ? 0: 1;

            updateButtons();

            try {
                const response = await fetch(`/perks/${perkId}/save`, {
                    method: "POST",
                    headers: {
                        [csrfHeader]: csrfToken,
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    credentials: "same-origin"
                });

                if (!response.ok) throw new Error("Perk failed to save!");
            } catch (err) {
                console.error(err);
                alert("Perk failed to save. Reverting...");
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

document.addEventListener("DOMContentLoaded", initPerksSave);

if (typeof module === 'object') {
    module.exports = initPerksSave;
}