const stage = document.querySelector('.deck-stage');
const slides = Array.from(stage.querySelectorAll('.slide'));
const prev = document.querySelector('#prev');
const next = document.querySelector('#next');
const full = document.querySelector('#full');
let current;
function fit() {
  const scale = Math.min(innerWidth / 1920, innerHeight / 1080);
  stage.style.transform = 'translate(' + (innerWidth - 1920 * scale) / 2 + 'px,' + (innerHeight - 1080 * scale) / 2 + 'px) scale(' + scale + ')';
}
function show(index) {
  current = Math.max(0, Math.min(slides.length - 1, index));
  slides.forEach((slide, i) => {
    slide.classList.toggle('active', i === current);
    slide.setAttribute('aria-hidden', String(i !== current));
  });
  prev.disabled = current === 0;
  next.disabled = current === slides.length - 1;
  if (location.hash !== '#' + (current + 1)) location.replace('#' + (current + 1));
}
async function fullscreen() {
  try {
    if (document.fullscreenElement) await document.exitFullscreen();
    else await document.documentElement.requestFullscreen();
  } catch { full.textContent = 'Utiliser F11'; }
}
document.addEventListener('fullscreenchange', () => { full.textContent = document.fullscreenElement ? 'Quitter le plein écran' : 'Plein écran'; });
prev.onclick = () => show(current - 1);
next.onclick = () => show(current + 1);
full.onclick = fullscreen;
document.addEventListener('keydown', event => {
  if (event.altKey || event.ctrlKey || event.metaKey) return;
  if (event.key === ' ' && event.target instanceof HTMLButtonElement) return;
  if (['ArrowRight','PageDown',' '].includes(event.key)) show(current + 1);
  else if (['ArrowLeft','PageUp'].includes(event.key)) show(current - 1);
  else if (event.key === 'Home') show(0);
  else if (event.key === 'End') show(slides.length - 1);
  else if (event.key.toLowerCase() === 'f') fullscreen();
  else return;
  event.preventDefault();
});
function restore() {
  const requested = Number(location.hash.slice(1));
  show(Number.isInteger(requested) ? requested - 1 : 0);
}
addEventListener('hashchange', restore);
addEventListener('resize', fit);
fit();
restore();

const replay = document.querySelector('#turn');
function replayTurn() {
  if (current !== 2) return;
  slides[current].querySelectorAll('.meter i').forEach(bar => {
    bar.getAnimations().forEach(animation => { animation.currentTime = 0; animation.play(); });
  });
}
replay.onclick = replayTurn;
document.addEventListener('keydown', event => {
  if (!event.altKey && !event.ctrlKey && !event.metaKey && event.key.toLowerCase() === 't') replayTurn();
});
