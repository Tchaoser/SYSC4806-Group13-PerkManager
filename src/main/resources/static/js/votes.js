function initVotes() {
    document.querySelectorAll(".vote-forms").forEach(container => {
        const loggedIn = container.dataset.loggedIn === "true";
        const voteState = parseInt(container.dataset.voteState) || 0;

        const perkId = container.dataset.perkId;
        const csrfHeader = container.dataset.csrfHeader;
        const csrfToken = container.dataset.csrfToken;

        const upBtn = container.querySelector('form[action*="/upvote"] button');
        const downBtn = container.querySelector('form[action*="/downvote"] button');

        const ratingCell = container.closest("tr").querySelector("td:nth-child(10)");
        const displayedRating = parseInt(ratingCell.textContent);
        const baseRating = displayedRating - voteState;
        let currentVote = voteState;

        const updateButtons = () => {
            upBtn.classList.toggle("voted", currentVote === 1);
            downBtn.classList.toggle("voted", currentVote === -1);
        };

        const handleVote = async (type) => {
            if (!loggedIn) return alert("You must be logged in to vote");

            const oldVote = currentVote;
            const oldRating = parseInt(ratingCell.textContent);

            if (type === "upvote") currentVote = currentVote === 1 ? 0 : 1;
            else if (type === "downvote") currentVote = currentVote === -1 ? 0 : -1;

            let newRating;
            if (currentVote === 1) newRating = baseRating + 1;
            else if (currentVote === -1) newRating = baseRating - 1;
            else newRating = baseRating;

            ratingCell.textContent = newRating;
            updateButtons();

            try {
                const response = await fetch(`/perks/${perkId}/${type}`, {
                    method: "POST",
                    headers: {
                        [csrfHeader]: csrfToken,
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    credentials: "same-origin"
                });

                if (!response.ok) throw new Error("Vote failed");
            } catch (err) {
                console.error(err);
                alert("Voting failed. Reverting...");
                currentVote = oldVote;
                ratingCell.textContent = oldRating;
                updateButtons();
            }
        };

        upBtn.addEventListener("click", e => {
            e.preventDefault();
            handleVote("upvote");
        });

        downBtn.addEventListener("click", e => {
            e.preventDefault();
            handleVote("downvote");
        });

        updateButtons();
    });
}

document.addEventListener("DOMContentLoaded", initVotes);
