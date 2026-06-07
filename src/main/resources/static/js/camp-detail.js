// Camp detail page: loads a single camp + its post-camp stats from the REST API.
(function () {
    const section = document.querySelector('[data-camp-id]');
    if (!section) {
        return;
    }
    const campId = section.getAttribute('data-camp-id');
    const detailEl = document.getElementById('campDetail');
    const statsEl = document.getElementById('campStats');

    function esc(value) {
        const span = document.createElement('span');
        span.textContent = value == null ? '' : String(value);
        return span.innerHTML;
    }

    function fmtTime(t) {
        return t ? t.substring(0, 5) : '';
    }

    function statusBadge(status) {
        const map = { UPCOMING: 'bg-primary', COMPLETED: 'bg-success', CANCELLED: 'bg-secondary' };
        return `<span class="badge ${map[status] || 'bg-secondary'}">${esc(status)}</span>`;
    }

    async function loadDetail() {
        try {
            const resp = await fetch(`/api/camps/${campId}`, { headers: { 'Accept': 'application/json' } });
            if (!resp.ok) {
                throw new Error('HTTP ' + resp.status);
            }
            const c = await resp.json();
            renderDetail(c);
            if (c.status === 'COMPLETED') {
                loadStats();
            }
        } catch (e) {
            detailEl.innerHTML =
                '<div class="alert alert-danger">Could not load camp details. Please try again.</div>';
        }
    }

    function renderDetail(c) {
        const time = c.startTime ? `${fmtTime(c.startTime)} – ${fmtTime(c.endTime)}` : 'To be announced';
        const capacity = c.capacity != null ? c.capacity : '—';
        detailEl.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start flex-wrap">
                        <h3 class="text-jd mb-2">${esc(c.name)}</h3>
                        <div>${statusBadge(c.status)}</div>
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-md-6"><i class="bi bi-calendar-event text-jd"></i>
                            <strong>Date:</strong> ${esc(c.campDate)}</div>
                        <div class="col-md-6"><i class="bi bi-clock text-jd"></i>
                            <strong>Time:</strong> ${esc(time)}</div>
                        <div class="col-md-6"><i class="bi bi-geo-alt text-jd"></i>
                            <strong>Venue / Location:</strong> ${esc(c.venue)}</div>
                        <div class="col-md-6"><i class="bi bi-map text-jd"></i>
                            <strong>District:</strong> ${esc(c.district)}</div>
                        <div class="col-md-6"><i class="bi bi-people text-jd"></i>
                            <strong>Registrations:</strong> ${esc(c.registrationCount)} / ${esc(capacity)}</div>
                        <div class="col-md-6"><i class="bi bi-building text-jd"></i>
                            <strong>Organizer:</strong> ${esc(c.organizerName)}</div>
                        <div class="col-md-6"><i class="bi bi-person text-jd"></i>
                            <strong>Contact Person:</strong> ${esc(c.organizerContact)}</div>
                        <div class="col-md-6"><i class="bi bi-telephone text-jd"></i>
                            <strong>Contact:</strong> ${esc(c.organizerPhone)} · ${esc(c.organizerEmail)}</div>
                    </div>
                    ${c.description ? `<p class="mt-3 mb-0 text-muted">${esc(c.description)}</p>` : ''}
                    <div class="mt-3">
                        <a href="/donor/login" class="btn btn-jd">
                            <i class="bi bi-droplet-fill"></i> Log in to Register</a>
                    </div>
                </div>
            </div>`;
    }

    async function loadStats() {
        try {
            const resp = await fetch(`/api/camps/${campId}/stats`, { headers: { 'Accept': 'application/json' } });
            if (!resp.ok) {
                return;
            }
            const s = await resp.json();
            const rows = (s.byBloodGroup || []).map(b =>
                `<tr><td><span class="badge badge-blood">${esc(b.bloodGroup)}</span></td>
                 <td>${esc(b.donors)}</td><td>${esc(b.units)} ml</td></tr>`).join('');
            statsEl.innerHTML = `
                <div class="card"><div class="card-body">
                    <h5 class="text-jd"><i class="bi bi-bar-chart"></i> Post-Camp Statistics</h5>
                    <div class="row text-center my-3">
                        <div class="col-4"><h4 class="text-jd mb-0">${esc(s.totalRegistrations)}</h4>
                            <small class="text-muted">Registrations</small></div>
                        <div class="col-4"><h4 class="text-jd mb-0">${esc(s.totalDonors)}</h4>
                            <small class="text-muted">Donors</small></div>
                        <div class="col-4"><h4 class="text-jd mb-0">${esc(s.totalUnits)} ml</h4>
                            <small class="text-muted">Units Collected</small></div>
                    </div>
                    ${rows ? `<table class="table table-sm"><thead><tr>
                        <th>Blood Group</th><th>Donors</th><th>Units</th></tr></thead>
                        <tbody>${rows}</tbody></table>` : ''}
                </div></div>`;
        } catch (e) {
            /* stats are optional */
        }
    }

    loadDetail();
})();
