const state = {
  packages: [],
  selectedPackage: null,
  booking: null,
  payment: null,
  user: JSON.parse(localStorage.getItem('travelUser') || 'null'),
  token: localStorage.getItem('travelToken') || ''
};

const app = document.getElementById('app');
const toast = document.getElementById('toast');

function esc(v) {
  return String(v ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[c]));
}
function money(v) { return `₹${Number(v || 0).toLocaleString('en-IN')}`; }
function saveSession(data) {
  state.user = {id:data.id, name:data.name, email:data.email, phone:data.phone || ''};
  state.token = data.token || '';
  localStorage.setItem('travelUser', JSON.stringify(state.user));
  if (state.token) localStorage.setItem('travelToken', state.token);
}
function clearSession() {
  state.user = null; state.token = '';
  localStorage.removeItem('travelUser'); localStorage.removeItem('travelToken');
}
function isLoggedIn() { return Boolean(state.user && state.token); }
function showToast(message, type='success') {
  toast.textContent = message; toast.className = `toast show ${type}`;
  setTimeout(() => toast.className = 'toast', 2800);
}

async function api(path, options={}) {
  const headers = {'Content-Type':'application/json', ...(options.headers || {})};
  if (state.token) headers['X-Auth-Token'] = state.token;
  const res = await fetch(path, {...options, headers});
  let data = null;
  try { data = await res.json(); } catch (_) {}
  if (!res.ok) throw new Error(data?.error || data?.message || `Request failed (${res.status})`);
  return data;
}

function navigate(path) {
  history.pushState({}, '', `#${path}`);
  render(); window.scrollTo({top:0, behavior:'smooth'});
}
window.addEventListener('popstate', render);
window.addEventListener('hashchange', render);

function route() { return location.hash.replace(/^#/, '') || '/'; }

function shell(content, active='') {
  return `
  <header class="topbar">
    <div class="nav-wrap">
      <a class="logo" href="#/" onclick="return go('/')"><span class="logo-mark">✈</span><span>Travel<span>Explorer</span></span></a>
      <nav class="nav-links">
        <a class="${active==='home'?'active':''}" href="#/" onclick="return go('/')">Home</a>
        <a class="${active==='packages'?'active':''}" href="#/packages" onclick="return go('/packages')">Explore</a>
        ${isLoggedIn() ? `<a class="${active==='bookings'?'active':''}" href="#/bookings" onclick="return go('/bookings')">My Bookings</a>` : ''}
      </nav>
      <div class="nav-actions">
        ${isLoggedIn() ? `<div class="user-chip">👋 ${esc(state.user.name.split(' ')[0])}</div><button class="nav-btn ghost" onclick="logout()">Logout</button>` : `<button class="nav-btn ghost" onclick="go('/login')">Login</button><button class="nav-btn" onclick="go('/register')">Register</button>`}
      </div>
    </div>
  </header>
  <main>${content}</main>
  <footer><div><strong>TravelExplorer</strong> • Tourism & Travel Booking System</div><div>Microservices • Factory • Builder</div></footer>`;
}
function go(path) { navigate(path); return false; }

function homeView() {
  return shell(`
    <section class="hero-home">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <span class="eyebrow">YOUR NEXT ADVENTURE STARTS HERE</span>
        <h1>Travel more.<br><em>Live more.</em></h1>
        <p>Discover beautiful destinations, handpicked stays and unforgettable experiences across India.</p>
        <div class="hero-actions"><button class="primary large" onclick="go('/packages')">Explore Packages <span>→</span></button><button class="outline-light large" onclick="${isLoggedIn()?'go(\'/bookings\')':'go(\'/register\')'}">${isLoggedIn()?'View My Bookings':'Create an Account'}</button></div>
      </div>
      <div class="hero-stats"><div><strong>3+</strong><span>Curated trips</span></div><div><strong>4.9/5</strong><span>Traveler rating</span></div><div><strong>24/7</strong><span>Booking access</span></div></div>
    </section>
    <section class="section intro"><div class="section-heading"><div><span class="eyebrow dark">PLAN YOUR ESCAPE</span><h2>Popular destinations</h2></div><button class="text-link" onclick="go('/packages')">View all packages →</button></div><div id="homePackages" class="cards-grid compact"><div class="loading-card">Loading destinations...</div></div></section>
    <section class="section features"><div class="feature"><div class="feature-icon">🧭</div><h3>Easy discovery</h3><p>Browse curated destinations with clear prices, stays and inclusions.</p></div><div class="feature"><div class="feature-icon">🔐</div><h3>Secure account</h3><p>Register and sign in to keep your bookings in one place.</p></div><div class="feature"><div class="feature-icon">⚡</div><h3>Simple booking</h3><p>Choose a trip, confirm travellers and complete simulated payment.</p></div></section>
  `,'home');
}

async function loadPackages() {
  if (state.packages.length) return state.packages;
  state.packages = await api('/api/packages');
  return state.packages;
}
function packageCard(p, featured=false) {
  const emoji = ({Goa:'🏖️',Manali:'🏔️',Kerala:'🌴'})[p.destination] || '🌍';
  return `<article class="package-card ${featured?'featured':''}">
    <div class="package-image ${p.destination.toLowerCase()}"><span>${emoji}</span><div class="pill">${p.days}D / ${p.nights}N</div></div>
    <div class="package-body"><div class="location">📍 ${esc(p.destination)}</div><h3>${esc(p.title)}</h3><p>${esc(p.description)}</p><div class="amenities"><span>${p.hotel ? '🏨 Hotel' : '🏨 Stay'}</span><span>${p.transportIncluded?'🚐 Transport':'🚫 No transport'}</span><span>${p.sightseeingIncluded?'📸 Sightseeing':'• Flexible'}</span></div><div class="package-footer"><div><small>From</small><strong>${money(p.price)}</strong><small>/ person</small></div><button class="small-btn" onclick="go('/package/${p.id}')">View Details</button></div></div>
  </article>`;
}
async function packagesView() {
  try {
    const ps = await loadPackages();
    return shell(`<section class="page-head"><span class="eyebrow dark">DISCOVER INDIA</span><h1>Find your next escape</h1><p>Choose from our curated travel packages and make your next journey memorable.</p></section><section class="section package-section"><div class="filter-bar"><div class="search-box">🔎 <input id="packageSearch" placeholder="Search destination or package..." oninput="filterPackages()"></div><div class="filter-label">${ps.length} packages available</div></div><div id="packageGrid" class="cards-grid">${ps.map(p=>packageCard(p)).join('')}</div></section>`,'packages');
  } catch(e) { return shell(`<div class="center-error"><h2>Unable to load packages</h2><p>${esc(e.message)}</p><button class="primary" onclick="render()">Try again</button></div>`,'packages'); }
}
function filterPackages() {
  const q = document.getElementById('packageSearch').value.toLowerCase();
  document.querySelectorAll('#packageGrid .package-card').forEach(card => card.style.display = card.innerText.toLowerCase().includes(q) ? '' : 'none');
}

async function packageView(id) {
  try {
    const p = await api(`/api/packages/${id}`); state.selectedPackage = p;
    const emoji = ({Goa:'🏖️',Manali:'🏔️',Kerala:'🌴'})[p.destination] || '🌍';
    return shell(`<section class="detail-wrap"><button class="back-link" onclick="go('/packages')">← Back to packages</button><div class="detail-grid"><div class="detail-visual ${p.destination.toLowerCase()}"><span>${emoji}</span><div class="detail-badge">${p.days} Days • ${p.nights} Nights</div></div><div class="detail-content"><span class="eyebrow dark">${esc(p.destination.toUpperCase())}</span><h1>${esc(p.title)}</h1><p class="lead">${esc(p.description)}</p><div class="info-grid"><div><span>🏨</span><small>Stay</small><strong>${esc(p.hotel || 'Hotel included')}</strong></div><div><span>🚐</span><small>Transport</small><strong>${p.transportIncluded?'Included':'Not included'}</strong></div><div><span>📸</span><small>Sightseeing</small><strong>${p.sightseeingIncluded?'Included':'Optional'}</strong></div><div><span>🗓️</span><small>Duration</small><strong>${p.days}D / ${p.nights}N</strong></div></div><div class="book-cta"><div><small>Package price</small><strong>${money(p.price)}</strong><span>per traveller</span></div><button class="primary large" onclick="requireLogin('/booking/${p.id}')">Book this trip →</button></div></div></div></section>`,'packages');
  } catch(e) { return shell(`<div class="center-error"><h2>Package not found</h2><p>${esc(e.message)}</p><button class="primary" onclick="go('/packages')">Back to packages</button></div>`); }
}
function requireLogin(path) { if (!isLoggedIn()) { sessionStorage.setItem('afterLogin', path); navigate('/login'); } else navigate(path); }

function authView(type) {
  const login = type === 'login';
  return shell(`<section class="auth-page"><div class="auth-card"><div class="auth-brand">✈️ <span>TravelExplorer</span></div><span class="eyebrow dark">${login?'WELCOME BACK':'START YOUR JOURNEY'}</span><h1>${login?'Sign in to your account':'Create your account'}</h1><p>${login?'Access your trips and manage your bookings.':'Join TravelExplorer and keep every trip in one place.'}</p><form onsubmit="${login?'login(event)':'register(event)'}">${!login?`<label>Full name<input id="name" required placeholder="Your full name"></label>`:''}<label>Email address<input id="email" type="email" required placeholder="you@example.com"></label>${!login?`<label>Phone number<input id="phone" placeholder="10-digit mobile number"></label>`:''}<label>Password<input id="password" type="password" minlength="6" required placeholder="At least 6 characters"></label>${!login?`<label>Confirm password<input id="confirm" type="password" minlength="6" required placeholder="Repeat your password"></label>`:''}<button class="primary full" type="submit">${login?'Sign In':'Create Account'} <span>→</span></button></form><div class="auth-switch">${login?`New to TravelExplorer? <a href="#/register" onclick="return go('/register')">Create an account</a>`:`Already have an account? <a href="#/login" onclick="return go('/login')">Sign in</a>`}</div><div class="auth-note">🔒 Your password is securely hashed by the authentication service.</div></div></section>`);
}
async function register(e) {
  e.preventDefault();
  const password = document.getElementById('password').value;
  if (password !== document.getElementById('confirm').value) return showToast('Passwords do not match','error');
  try { await api('/api/auth/register',{method:'POST',body:JSON.stringify({name:document.getElementById('name').value,email:document.getElementById('email').value,password,phone:document.getElementById('phone').value})}); showToast('Account created. Please sign in.'); navigate('/login'); }
  catch(err){ showToast(err.message,'error'); }
}
async function login(e) {
  e.preventDefault();
  try { const data = await api('/api/auth/login',{method:'POST',body:JSON.stringify({email:document.getElementById('email').value,password:document.getElementById('password').value})}); saveSession(data); showToast(`Welcome, ${data.name.split(' ')[0]}!`); const next=sessionStorage.getItem('afterLogin') || '/'; sessionStorage.removeItem('afterLogin'); navigate(next); }
  catch(err){ showToast(err.message,'error'); }
}
async function logout() { try { if(state.token) await api('/api/auth/logout',{method:'POST'}); } catch(_) {} clearSession(); showToast('You have been logged out.'); navigate('/'); }

async function bookingView(id) {
  if (!isLoggedIn()) return authView('login');
  let p = state.selectedPackage;
  if (!p || String(p.id) !== String(id)) p = await api(`/api/packages/${id}`);
  state.selectedPackage = p;
  return shell(`<section class="form-page"><button class="back-link" onclick="go('/package/${p.id}')">← Back to package</button><div class="checkout-grid"><div class="checkout-main"><span class="eyebrow dark">STEP 1 OF 2</span><h1>Booking details</h1><p class="lead">Tell us who is travelling and how you'd like to book.</p><form onsubmit="createBooking(event,${p.id})" class="checkout-form"><div class="form-card"><h3>Traveller details</h3><div class="form-row"><label>Full name<input value="${esc(state.user.name)}" disabled></label><label>Email<input value="${esc(state.user.email)}" disabled></label></div><div class="form-row"><label>Number of travellers<select id="travellers"><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option><option>6</option><option>7</option><option>8</option></select></label><label>Booking type<select id="bookingType"><option value="TOUR">Tour package</option><option value="HOTEL">Hotel</option><option value="FLIGHT">Flight</option></select></label></div></div><div class="form-card"><h3>Trip preferences</h3><div class="preference"><span>✓</span><div><strong>${esc(p.hotel)}</strong><small>Accommodation included in this package</small></div></div><div class="preference"><span>${p.transportIncluded?'✓':'—'}</span><div><strong>Transport ${p.transportIncluded?'included':'not included'}</strong><small>As listed in the package details</small></div></div></div><button class="primary large full" type="submit">Continue to payment →</button></form></div><aside class="summary-card"><div class="summary-image ${p.destination.toLowerCase()}">${({Goa:'🏖️',Manali:'🏔️',Kerala:'🌴'})[p.destination]||'🌍'}</div><div class="summary-body"><span class="location">📍 ${esc(p.destination)}</span><h3>${esc(p.title)}</h3><p>${p.days} Days / ${p.nights} Nights</p><div class="summary-line"><span>Price per traveller</span><strong>${money(p.price)}</strong></div><div class="summary-line total"><span>Total</span><strong id="liveTotal">${money(p.price)}</strong></div></div></aside></div></section>`,'');
}
async function createBooking(e,id) {
  e.preventDefault();
  const travellers = Number(document.getElementById('travellers').value);
  const bookingType = document.getElementById('bookingType').value;
  try { const data=await api('/api/bookings',{method:'POST',body:JSON.stringify({packageId:id,bookingType,travellers})}); state.booking=data; state.payment=null; navigate(`/payment/${data.id}`); }
  catch(err){ showToast(err.message,'error'); }
}

async function paymentView(id) {
  if (!isLoggedIn()) return authView('login');
  if (!state.booking || String(state.booking.id)!==String(id)) {
    try { const list=await api('/api/bookings'); state.booking=list.find(b=>String(b.id)===String(id)); } catch(_) {}
  }
  const b=state.booking;
  if (!b) return shell(`<div class="center-error"><h2>Booking not found</h2><button class="primary" onclick="go('/bookings')">My Bookings</button></div>`);
  const p = state.selectedPackage;
  return shell(`<section class="form-page"><div class="checkout-grid single"><div class="checkout-main"><span class="eyebrow dark">STEP 2 OF 2</span><h1>Complete your payment</h1><p class="lead">Your booking is reserved. Choose a payment method to complete the simulated transaction.</p><div class="form-card payment-methods"><h3>Payment method</h3><label class="radio-card"><input type="radio" name="method" value="UPI" checked><span>📱</span><div><strong>UPI</strong><small>Google Pay, PhonePe, Paytm and more</small></div></label><label class="radio-card"><input type="radio" name="method" value="CARD"><span>💳</span><div><strong>Credit / Debit Card</strong><small>Visa, Mastercard and RuPay</small></div></label><label class="radio-card"><input type="radio" name="method" value="NETBANKING"><span>🏦</span><div><strong>Net Banking</strong><small>All major Indian banks</small></div></label><button class="primary large full" onclick="completePayment(${b.id},${b.totalAmount})">Pay ${money(b.totalAmount)} →</button><p class="secure-note">🔒 Demo payment • No real money is charged</p></div></div><aside class="summary-card"><div class="summary-body"><span class="eyebrow dark">BOOKING #${b.id}</span><h3>${p?esc(p.title):`Package ${b.packageId}`}</h3><div class="summary-line"><span>Travellers</span><strong>${b.travellers}</strong></div><div class="summary-line"><span>Booking type</span><strong>${esc(b.bookingType)}</strong></div><div class="summary-line total"><span>Amount</span><strong>${money(b.totalAmount)}</strong></div></div></aside></div></section>`);
}
async function completePayment(bookingId, amount) {
  const method = document.querySelector('input[name="method"]:checked').value;
  try { const data=await api('/api/payments',{method:'POST',body:JSON.stringify({bookingId,amount,method})}); state.payment=data; navigate(`/confirmation/${bookingId}`); }
  catch(err){ showToast(err.message,'error'); }
}

async function confirmationView(id) {
  if (!isLoggedIn()) return authView('login');
  const b=state.booking || (await api('/api/bookings')).find(x=>String(x.id)===String(id));
  if (!b) return shell(`<div class="center-error"><h2>Booking not found</h2></div>`);
  return shell(`<section class="confirmation"><div class="success-circle">✓</div><span class="eyebrow dark">BOOKING CONFIRMED</span><h1>Your trip is booked!</h1><p class="lead">You're all set, ${esc(state.user.name.split(' ')[0])}. We hope you have an amazing journey.</p><div class="ticket"><div class="ticket-top"><div><small>BOOKING ID</small><strong>#TRV-${String(b.id).padStart(5,'0')}</strong></div><span class="status">CONFIRMED</span></div><div class="ticket-destination"><div class="ticket-icon">${({Goa:'🏖️',Manali:'🏔️',Kerala:'🌴'})[state.selectedPackage?.destination]||'✈️'}</div><div><small>YOUR TRIP</small><h3>${esc(state.selectedPackage?.title || `Package ${b.packageId}`)}</h3><p>${b.travellers} traveller(s) • ${esc(b.bookingType)}</p></div></div><div class="ticket-bottom"><div><small>TOTAL PAID</small><strong>${money(b.totalAmount)}</strong></div><div><small>PAYMENT</small><strong>${esc(state.payment?.method || 'SUCCESS')}</strong></div><div><small>TRANSACTION</small><strong>${esc(state.payment?.transactionId || 'Recorded')}</strong></div></div></div><div class="confirmation-actions"><button class="primary" onclick="go('/bookings')">View My Bookings</button><button class="outline" onclick="go('/packages')">Explore More Trips</button></div></section>`);
}

async function bookingsView() {
  if (!isLoggedIn()) return authView('login');
  try {
    const [bookings, packages] = await Promise.all([api('/api/bookings'), loadPackages()]);
    const map = Object.fromEntries(packages.map(p=>[p.id,p]));
    return shell(`<section class="page-head bookings-head"><span class="eyebrow dark">YOUR TRAVEL DASHBOARD</span><h1>My bookings</h1><p>Keep track of upcoming adventures and past trips.</p></section><section class="section bookings-section">${bookings.length ? `<div class="booking-list">${bookings.map(b=>bookingRow(b,map[b.packageId])).join('')}</div>` : `<div class="empty-state"><div>🧳</div><h2>No trips booked yet</h2><p>Your next adventure is waiting for you.</p><button class="primary" onclick="go('/packages')">Explore Packages</button></div>`}</section>`,'bookings');
  } catch(e) { return shell(`<div class="center-error"><h2>Could not load bookings</h2><p>${esc(e.message)}</p></div>`,'bookings'); }
}
function bookingRow(b,p) {
  return `<article class="booking-card"><div class="booking-thumb ${p?.destination?.toLowerCase()||''}">${({Goa:'🏖️',Manali:'🏔️',Kerala:'🌴'})[p?.destination]||'✈️'}</div><div class="booking-info"><div class="booking-top"><span class="status ${b.status==='CANCELLED'?'cancelled':''}">${esc(b.status)}</span><small>Booking #${b.id}</small></div><h3>${esc(p?.title || `Package ${b.packageId}`)}</h3><p>📍 ${esc(p?.destination || 'India')} • ${b.travellers} traveller(s) • ${esc(b.bookingType)}</p><small>${b.bookingDate ? new Date(b.bookingDate).toLocaleString('en-IN',{dateStyle:'medium',timeStyle:'short'}) : ''}</small></div><div class="booking-price"><small>Total</small><strong>${money(b.totalAmount)}</strong>${b.status!=='CANCELLED'?`<button class="text-link danger" onclick="cancelBooking(${b.id})">Cancel booking</button>`:''}</div></article>`;
}
async function cancelBooking(id) { if(!confirm('Cancel this booking?')) return; try { await api(`/api/bookings/${id}/cancel`,{method:'PUT'}); showToast('Booking cancelled.','success'); render(); } catch(e){ showToast(e.message,'error'); } }

async function render() {
  const r = route();
  try {
    if (r === '/') { app.innerHTML = homeView(); const ps=await loadPackages(); const el=document.getElementById('homePackages'); if(el) el.innerHTML=ps.slice(0,3).map(p=>packageCard(p,true)).join(''); return; }
    if (r === '/packages') { app.innerHTML = await packagesView(); return; }
    if (r === '/login') { app.innerHTML = authView('login'); return; }
    if (r === '/register') { app.innerHTML = authView('register'); return; }
    if (r === '/bookings') { app.innerHTML = await bookingsView(); return; }
    const m=r.match(/^\/package\/(\d+)$/); if(m){ app.innerHTML=await packageView(m[1]); return; }
    const b=r.match(/^\/booking\/(\d+)$/); if(b){ app.innerHTML=await bookingView(b[1]); return; }
    const pay=r.match(/^\/payment\/(\d+)$/); if(pay){ app.innerHTML=await paymentView(pay[1]); return; }
    const c=r.match(/^\/confirmation\/(\d+)$/); if(c){ app.innerHTML=await confirmationView(c[1]); return; }
    app.innerHTML=shell(`<div class="center-error"><h2>Page not found</h2><button class="primary" onclick="go('/')">Go Home</button></div>`);
  } catch(e) { app.innerHTML=shell(`<div class="center-error"><h2>Something went wrong</h2><p>${esc(e.message)}</p><button class="primary" onclick="go('/')">Go Home</button></div>`); }
}

render();
