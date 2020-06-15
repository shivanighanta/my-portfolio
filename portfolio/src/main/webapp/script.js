
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Fetches a random quote from the server and adds it to the DOM.
 */
function getRandomQuote() {
  console.log('Fetching a random quote.');

  // The fetch() function returns a Promise because the request is asynchronous.
  const responsePromise = fetch('/data');

  // When the request is complete, pass the response into handleResponse().
  responsePromise.then(handleResponse);
}

/**
 * Handles response by converting it to text and passing the result to
 * addQuoteToDom().
 */
function handleResponse(response) {
  console.log('Handling the response.');

  // response.text() returns a Promise, because the response is a stream of
  // content and not a simple variable.
  const textPromise = response.text();

  // When the response is converted to text, pass the result into the
  // addQuoteToDom() function.
  textPromise.then(addQuoteToDom);
}

/** Adds a random quote to the DOM. */
function addQuoteToDom(quote) {
  console.log('Adding quote to dom: ' + quote);
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

/** Adds a map with markers to the page. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 40.113926, lng: -88.224927}, zoom: 16});

  addMarker(
      map, 40.113926, -88.224927, 'Siebel',
      '<img src=\'images/siebel.png\'><div>Seiebel Center for Computer Science: Where all the CS classes are held and my second home (also has the best bagels)');

  addMarker(
      map, 40.109331, -88.227223, 'Historical Illini Union',
      '<img src=\'images/union.jpg\'><div>This historical building is located at the center of campus</div>');

  addMarker(
      map, 40.106125, -88.227289, 'Foellinger Auditorium',
      '<img src=\'images/foellinger.jpg\'><div>Biggest lecture hall on campus, also where famous people give speeches (Caught a glimpse of Obama once)</div>');

  addMarker(
      map, 40.111838, -88.230963, 'Chipotle',
      '<img src=\'images/chipotle.jpg\'><div>Love of my life. nuff said.</div>');
}

/** Adds a marker that displays an info window on click. */
function addMarker(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}
