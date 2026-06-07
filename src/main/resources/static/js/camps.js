// Live district-wise camp search backed by the REST API (/api/camps, /api/districts).
(function () {
    const input = document.getElementById('districtSearch');
    const results = document.getElementById('campResults');
    const noResults = document.getElementById('noResults');
    const statusEl = document.getElementById('status');
    const clearBtn = document.getElementById('clearBtn');
    const datalist = document.getElementById('districtList');

    if (!input || !results) {
        return;
    }

    function escapeHtml(value) {
        if (value == null) {
            return '';
        }
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    function formatDate(isoDate) {
        if (!isoDate) {
            return '';
        }
        const d = new Date(isoDate);
        if (isNaN(d.getTime())) {
            return isoDate;
        }
        return d.toLocaleDateString(undefined, { day: '2-digit', month: 'short', year: 'numeric' });
    }

    function campCard(camp) {
        return `
            <div class="col-md-4">
              <div class="card h-100">
                <div class="card-body">
                  <h5 class="card-title text-jd">${escapeHtml(camp.name)}</h5>
                  <p class="mb-1"><span class="badge bg-secondary">${escapeHtml(camp.district)}</span></p>
                  <p class="mb-1"><i class="bi bi-geo-alt"></i> ${escapeHtml(camp.venue)}</p>
                  <p class="mb-1"><i class="bi bi-calendar-event"></i> ${formatDate(camp.campDate)}</p>
                  <p class="mb-1"><i class="bi bi-building"></i> ${escapeHtml(camp.organizerName)}</p>
                  <p class="text-muted small">${escapeHtml(camp.description)}</p>
                </div>
                <div class="card-footer bg-white border-0">
                  <a href="/donor/login" class="btn btn-sm btn-jd w-100">Login to Register</a>
                </div>
              </div>
            </div>`;
    }

    async function loadCamps(district) {
        statusEl.textContent = 'Loading camps...';
        try {
            const url = district
                ? `/api/camps?district=${encodeURIComponent(district)}`
                : '/api/camps';
            const resp = await fetch(url, { headers: { 'Accept': 'application/json' } });
            if (!resp.ok) {
                throw new Error('Request failed: ' + resp.status);
            }
            const camps = await resp.json();
            render(camps);
            statusEl.textContent = `${camps.length} camp(s) found`
                + (district ? ` in "${district}"` : '');
        } catch (err) {
            statusEl.textContent = '';
            results.innerHTML =
                '<div class="col-12"><div class="alert alert-danger">Could not load camps. '
                + 'Please try again.</div></div>';
        }
    }

    function render(camps) {
        if (!camps || camps.length === 0) {
            results.innerHTML = '';
            noResults.classList.remove('d-none');
            return;
        }
        noResults.classList.add('d-none');
        results.innerHTML = camps.map(campCard).join('');
    }

    async function loadDistricts() {
        try {
            const resp = await fetch('/api/districts', { headers: { 'Accept': 'application/json' } });
            if (!resp.ok) {
                return;
            }
            const districts = await resp.json();
            datalist.innerHTML = districts
                .map(d => `<option value="${escapeHtml(d)}"></option>`)
                .join('');
        } catch (err) {
            /* non-fatal: suggestions just won't appear */
        }
    }

    // Debounced live search.
    let timer = null;
    input.addEventListener('input', function () {
        clearTimeout(timer);
        timer = setTimeout(() => loadCamps(input.value.trim()), 250);
    });

    clearBtn.addEventListener('click', function () {
        input.value = '';
        loadCamps('');
    });

    loadDistricts();
    loadCamps(input.value.trim());
})();
