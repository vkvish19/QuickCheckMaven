/* * File: TV_Watchlist.js
 * This is a template. The 'DATA_PLACEHOLDER' will be replaced by
 * BookmarkletGenerator with the content of vk_stocks.json.
 */
javascript:(function(){
    const DATA = {{DATA_PLACEHOLDER}};

    const STORAGE_KEY = 'tv_nested_states';
    let states = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}');

    const menuId = 'tv-nested-menu';
    const existing = document.getElementById(menuId);
    if (existing) { existing.remove(); return; }

    const m = document.createElement('div');
    m.id = menuId;
    m.style = 'position:fixed;top:50px;right:20px;width:240px;max-height:85vh;background:#131722;color:#d1d4dc;z-index:999999;overflow-y:auto;padding:15px;border:1px solid #363c4e;border-radius:8px;box-shadow:0 10px 40px rgba(0,0,0,0.6);font-family:sans-serif;user-select:none;';

    let html = '<div style="display:flex;justify-content:space-between;margin-bottom:10px;padding-bottom:5px;border-bottom:1px solid #333"><b style="color:#18bed1;font-size:14px;">VK WATCHLIST</b><span id="close-tv" style="cursor:pointer;color:#868993;font-weight:bold">X</span></div>';

    for (const [mainGrp, sectors] of Object.entries(DATA)) {
        const isMainExp = states[mainGrp] !== false;
        html += `
            <div class="lvl-1-hdr" data-id="${mainGrp}" style="background:#2962ff33; color:#b0f0f7; padding:6px; margin-top:8px; cursor:pointer; font-weight:bold; font-size:12px; display:flex; justify-content:space-between; border-radius:4px;">
                <span>${mainGrp}</span><span>${isMainExp ? '▼' : '▶'}</span>
            </div>
            <div id="cnt-${mainGrp}" style="display:${isMainExp ? 'block' : 'none'}; padding-left:8px;">`;

        for (const [sector, stocks] of Object.entries(sectors)) {
            const secKey = `${mainGrp}_${sector}`;
            const isSecExp = states[secKey] !== false;
            html += `
                <div class="lvl-2-hdr" data-id="${secKey}" data-parent="${mainGrp}" style="color:#85b1b6; font-size:11px; margin:8px 0 4px 0; font-weight:bold; cursor:pointer; display:flex; justify-content:space-between; padding:2px 4px;">
                    <span>${sector.toUpperCase()}</span><span>${isSecExp ? '▼' : '▶'}</span>
                </div>
                <div id="cnt-${secKey}" style="display:${isSecExp ? 'block' : 'none'}">`;

            stocks.forEach(sym => {
                const n = sym.split(':')[1] || sym;
                html += `<div class="t-i" data-s="${sym}" style="padding:5px 10px; cursor:pointer; font-size:13px; border-radius:4px;">${n}</div>`;
            });
            html += `</div>`;
        }
        html += `</div>`;
    }

    m.innerHTML = html;
    document.body.appendChild(m);

    const st = document.createElement('style');
    st.innerHTML = '.t-i:hover{background:#2a2e39;color:#2962ff} .lvl-1-hdr:hover{background:#2962ff55} .lvl-2-hdr:hover{color:#fff}';
    document.head.appendChild(st);

    m.onclick = (e) => {
        const target = e.target.closest('.lvl-1-hdr, .lvl-2-hdr, .t-i, #close-tv');
        if (!target) return;
        if (target.id === 'close-tv') { m.remove(); return; }
        if (target.classList.contains('t-i')) {
            const u = new URL(window.location.href);
            u.searchParams.set('symbol', target.getAttribute('data-s'));
            window.location.href = u.href;
            return;
        }
        const id = target.getAttribute('data-id');
        const content = document.getElementById(`cnt-${id}`);
        const arrow = target.querySelectorAll('span')[1];
        const isCurrentlyOpen = content.style.display === 'block';
        content.style.display = isCurrentlyOpen ? 'none' : 'block';
        arrow.innerText = isCurrentlyOpen ? '▶' : '▼';
        states[id] = !isCurrentlyOpen;

        if (target.classList.contains('lvl-1-hdr') && isCurrentlyOpen) {
            const parentName = id;
            if (DATA[parentName]) {
                Object.keys(DATA[parentName]).forEach(sectorName => {
                    const secKey = `${parentName}_${sectorName}`;
                    states[secKey] = false;
                    const childCnt = document.getElementById(`cnt-${secKey}`);
                    const childHdr = document.querySelector(`[data-id="${secKey}"]`);
                    if (childCnt) childCnt.style.display = 'none';
                    if (childHdr) childHdr.querySelectorAll('span')[1].innerText = '▶';
                });
            }
        }
        localStorage.setItem(STORAGE_KEY, JSON.stringify(states));
    };
})();