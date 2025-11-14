document.addEventListener("DOMContentLoaded", () => {
    if (!window.ALL_PERKS || !Array.isArray(window.ALL_PERKS)) return;

    const tableBody = document.querySelector("table tbody");
    if (!tableBody) return;

    let currentSortField = null;
    let currentDirection = "asc";

    document.querySelectorAll("th.sortable a").forEach(link => {
        link.addEventListener("click", e => {
            e.preventDefault();

            const url = new URL(link.href, window.location.origin);
            const sortField = url.searchParams.get("sort");
            const direction = url.searchParams.get("direction") || "asc";

            if (sortField === currentSortField && direction === currentDirection) return;

            currentSortField = sortField;
            currentDirection = direction;

            sortAndRender(sortField, direction);
        });
    });

    function sortAndRender(field, direction) {
        const perks = [...window.ALL_PERKS];

        perks.sort((a, b) => compare(a, b, field, direction));
        renderRows(perks);
    }

    function compare(a, b, field, direction) {
        let v1 = a[field];
        let v2 = b[field];

        if (field === "expiry") {
            v1 = v1 ? new Date(v1) : new Date(8640000000000000);
            v2 = v2 ? new Date(v2) : new Date(8640000000000000);
        }

        if (v1 == null) v1 = "";
        if (v2 == null) v2 = "";

        if (typeof v1 === "string") v1 = v1.toLowerCase();
        if (typeof v2 === "string") v2 = v2.toLowerCase();

        if (v1 < v2) return direction === "asc" ? -1 : 1;
        if (v1 > v2) return direction === "asc" ? 1 : -1;
        return 0;
    }

    function renderRows(perks) {
        tableBody.innerHTML = "";
        perks.forEach(perk => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${perk.benefit || ''}</td>
                <td>${perk.membership?.type || 'None'}</td>
                <td>${perk.membership?.description || 'None'}</td>
                <td>${perk.membership?.organizationName || 'None'}</td>
                <td>${perk.product?.name || 'None'}</td>
                <td>${perk.product?.company || 'None'}</td>
                <td>${perk.product?.description || ''}</td>
                <td>${perk.region || 'Global'}</td>
                <td>${perk.expiryDate ? new Date(perk.expiryDate).toISOString().split("T")[0] : 'No Expiry'}</td>
                <td>${perk.rating ?? 0}</td>
                <td>${generateVoteForms(perk)}</td>
            `;
            tableBody.appendChild(row);
        });

        if (typeof initVotes === "function") initVotes();
    }

    function generateVoteForms(perk) {
        const state = perk.voteState || 0;
        return `
            <div class="vote-forms" data-perk-id="${perk.id}" data-vote-state="${state}" data-logged-in="${perk.isAuthenticated}">
                <form action="/perks/${perk.id}/upvote" method="post">
                    <button class="btn-small upvote ${state === 1 ? "voted" : ""}">▲</button>
                </form>
                <form action="/perks/${perk.id}/downvote" method="post">
                    <button class="btn-small downvote ${state === -1 ? "voted" : ""}">▼</button>
                </form>
            </div>
        `;
    }
});
