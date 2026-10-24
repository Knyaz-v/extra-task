async function api(path, method, body) {
  const options = { method: method || 'GET' };
  if (body) {
    options.headers = { 'Content-Type': 'application/json' };
    options.body = JSON.stringify(body);
  }
  const res = await fetch(path, options);

  if (!res.ok) {
    let msg = 'Ошибка ' + res.status;
    try {
      const data = await res.json();
      if (data.message) msg = data.message;
    } catch (e) { }
    throw new Error(msg);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

function nav(active) {
  const links = [
    ['index.html', 'Книги'],
    ['genres.html', 'Жанры'],
    ['add-book.html', 'Добавить книгу'],
    ['loans.html', 'Выдачи']
  ];
  let html = '<nav>';
  for (const [href, text] of links) {
    html += href === active
      ? '<b>' + text + '</b>'
      : '<a href="' + href + '">' + text + '</a>';
    html += ' | ';
  }
  html += '</nav><hr>';
  document.body.insertAdjacentHTML('afterbegin', html);
}

function formatDate(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  const pad = n => String(n).padStart(2, '0');
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
       + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());
}