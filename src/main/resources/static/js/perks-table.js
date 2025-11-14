document.addEventListener("DOMContentLoaded", () => {
    const tableBody = document.querySelector("table tbody");
    const pagerDiv = document.querySelector(".pager"); // optional

    const cache = {};

    const getCacheKey = (url) => url;

    const updateTable = (html) => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");

        const newTbody = doc.querySelector("tbody");
        if (newTbody) tableBody.innerHTML = newTbody.innerHTML;

        const newPager = doc.querySelector(".pager");
        if (newPager && pagerDiv) pagerDiv.innerHTML = newPager.innerHTML;

        if (typeof initVotes === "function") initVotes();

        bindTableLinks();
    };

    const fetchTable = async (url) => {
        const key = getCacheKey(url);

        if (cache[key]) {
            updateTable(cache[key]);
            return;
        }

        try {
            const response = await fetch(url, {
                headers: { "X-Requested-With": "XMLHttpRequest" },
                credentials: "same-origin"
            });

            if (!response.ok) throw new Error("Failed to fetch perks");

            const html = await response.text();
            cache[key] = html; // cache result
            updateTable(html);

            preLoadNextPage(url);

        } catch (err) {
            console.error(err);
            alert("Failed to load perks");
        }
    };

    const preLoadNextPage = async (currentUrl) => {
        const urlObj = new URL(currentUrl, window.location.origin);
        const page = parseInt(urlObj.searchParams.get("page") || "0", 10);

        urlObj.searchParams.set("page", page + 1);
        const nextUrl = urlObj.toString();
        const key = getCacheKey(nextUrl);

        if (cache[key]) return; // already cached

        try {
            const resp = await fetch(nextUrl, {
                headers: { "X-Requested-With": "XMLHttpRequest" },
                credentials: "same-origin"
            });
            if (resp.ok) {
                cache[key] = await resp.text();
            }
        } catch (err) {
        }
    };

    const bindTableLinks = () => {
        const pageLinks = document.querySelectorAll(".pager a");
        const sortLinks = document.querySelectorAll("th.sortable a");

        pageLinks.forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                fetchTable(link.href);
            });
        });

        sortLinks.forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                fetchTable(link.href);
            });
        });
    };

    bindTableLinks();
});
