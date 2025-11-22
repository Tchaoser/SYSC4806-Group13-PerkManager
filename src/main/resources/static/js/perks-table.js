var tableBody;
var pagerDiv;
var cache;
var getCacheKey;
var inFlightFetch = null;
var module;

function setEventListeners() {
    document.addEventListener("DOMContentLoaded", () => {
        tableBody = document.querySelector("table tbody");
        pagerDiv = document.querySelector(".pager"); // optional

        if (!tableBody) {
            console.warn("No table body found! Sorting and pagination disabled.");
            return;
        }

        cache = {};
        getCacheKey = (url) => url;

        bindTableLinks();
    });
}

function updateTable(html) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, "text/html");

    const newTbody = doc.querySelector("tbody");
    if (!newTbody) {
        console.error("No tbody found in fetched HTML!");
        return;
    }
    tableBody.innerHTML = newTbody.innerHTML;

    if (pagerDiv) {
        const newPager = doc.querySelector(".pager");
        if (newPager) pagerDiv.innerHTML = newPager.innerHTML;
    }

    if (typeof initPerksSave === "function") initPerksSave();

    bindTableLinks();
}

async function fetchTable(url) {
    const key = getCacheKey(url);

    if (cache[key]) {
        updateTable(cache[key]);
        return;
    }

    if (inFlightFetch) {
        console.log("Another fetch in progress, ignoring this one:", url);
        return;
    }

    inFlightFetch = url;

    try {
        const response = await fetch(url, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            credentials: "same-origin"
        });

        if (!response.ok) throw new Error("Server returned " + response.status);

        const html = await response.text();
        cache[key] = html;
        updateTable(html);

        preLoadNextPage(url);
    } catch (err) {
        console.error("Failed to fetch perks:", err);
    } finally {
        inFlightFetch = null;
    }
}

async function preLoadNextPage(currentUrl) {
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
        console.warn("Failed to preload next page:", err);
    }
}

function bindTableLinks() {
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

            if (link.classList.contains("active-sort")) return;

            document.querySelectorAll("th.sortable a").forEach(a => a.classList.remove("active-sort"));
            link.classList.add("active-sort");

            fetchTable(link.href);
        });
    });
}

setEventListeners();

if (typeof module === 'object') {
    module.exports = {
        bindTableLinks: bindTableLinks,
        setEventListeners: setEventListeners,
        preLoadNextPage: preLoadNextPage,
        fetchTable: fetchTable,
        updateTable: updateTable
    }
}

