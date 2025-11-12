var document;

function add_perk() {
    const form = document.getElementById('addPerkForm');
    const expiry = document.getElementById('expiryDate');
    if (expiry) {
        const t = new Date(), y = t.getFullYear(), m = String(t.getMonth()+1).padStart(2,'0'), d = String(t.getDate()).padStart(2,'0');
        expiry.min = `${y}-${m}-${d}`;
    }
    const markValidity = (el) => {
        const ok = el.validity.valid;
        el.classList.toggle('is-invalid', !ok);
        el.setAttribute('aria-invalid', String(!ok));
        if (ok) el.setCustomValidity('');
    };

    form.addEventListener('input', (e) => {
        if (e.target instanceof HTMLElement && 'validity' in e.target) markValidity(e.target);
    });

    form.addEventListener('invalid', (e) => {
        if (e.target instanceof HTMLElement) markValidity(e.target);
    }, true);

    form.addEventListener('submit', function submit() {
        const firstBad = form.querySelector('.is-invalid');
        if (firstBad) firstBad.focus();
    });
};

// describe("test suite description here", function() {
//     beforeEach(function(){
//         const { JSDOM } = require("jsdom");
//         JSDOM.fromFile("templates/add-perk.html").then(dom => {
//                 document = dom.window.document
//             }
//         )
//     })
//
//     it("test case description here", function () {
//         const button = document.getElementById("save_perk")
//         console.log(document.contents())
//         console.log(button.contents())
//         expect(button).toExist()
//         const spyer = spyOnEvent(button, 'click')
//         button.click()
//         expect('click').toHaveBeenTriggeredOn(button)
//     });
//
// });

